package com.daken.project.service.impl;

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
            List<SensitiveWord> list = list();
            if(!CollectionUtils.isEmpty(list)){
                Set<String> sensitiveWordSet = list.stream()
                        .map(SensitiveWord::getWord)
                        .collect(Collectors.toSet());
                sensitiveWordUtils.loadWord(sensitiveWordSet);
            }
            log.info("[initSensitiveWord] end, load sensitiveWords num :{}", list.size());
        });
    }
}




