package com.daken.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daken.common.entity.SensitiveWord;
import com.daken.project.sensitive.SensitiveWordUtils;
import com.daken.project.service.SensitiveWordService;
import com.daken.project.mapper.SensitiveWordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author daken
* @description 针对表【sensitive_word】的数据库操作Service实现
* @createDate 2023-06-23 16:44:52
*/
@Service
@Slf4j
public class SensitiveWordServiceImpl extends ServiceImpl<SensitiveWordMapper, SensitiveWord>
    implements SensitiveWordService{

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private SensitiveWordUtils sensitiveWordUtils;

    /**
     * 异步读取敏感词，不影响主进程启动
     */
    @PostConstruct
    public void initSensitiveWord(){
        threadPoolTaskExecutor.execute(() -> {
            log.info("[initSensitiveWord] start !");
            // 获取敏感词
            QueryWrapper<SensitiveWord> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("word_type", 0);
            List<SensitiveWord> sensitiveWordList = list(queryWrapper1);
            if(!CollectionUtils.isEmpty(sensitiveWordList)){
                Set<String> sensitiveWordSet = sensitiveWordList.stream()
                        .map(SensitiveWord::getWord)
                        .collect(Collectors.toSet());
                sensitiveWordUtils.loadWord(sensitiveWordSet, true);
            }
            // 获取白名单
            QueryWrapper<SensitiveWord> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("word_type", 1);
            List<SensitiveWord> whiteList = list(queryWrapper2);
            if(!CollectionUtils.isEmpty(whiteList)){
                Set<String> whiteSet = whiteList.stream()
                        .map(SensitiveWord::getWord)
                        .collect(Collectors.toSet());
                sensitiveWordUtils.loadWord(whiteSet, false);
            }
            log.info("[initSensitiveWord] end, load sensitiveWords num :{}, white list num: {}", sensitiveWordList.size(), whiteList.size());
        });
    }
}




