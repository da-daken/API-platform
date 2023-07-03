package com.daken.project.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.daken.common.entity.InterfaceInfo;
import com.daken.common.entity.UserInterfaceInfo;
import com.daken.project.annotation.AuthCheck;
import com.daken.project.common.BaseResponse;
import com.daken.project.common.ErrorCode;
import com.daken.project.common.ResultUtils;
import com.daken.project.exception.BusinessException;
import com.daken.project.mapper.UserInterfaceInfoMapper;
import com.daken.project.model.vo.InterfaceInfoVo;
import com.daken.project.service.InterfaceInfoService;
import com.daken.project.utils.BeanCopyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *  分析控制器
 */
@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @GetMapping("/top/interface/invoke")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<InterfaceInfoVo>> listTopInvokeInterfaceInfo() {
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(10);
        Map<Long, List<UserInterfaceInfo>> idGroupMap = userInterfaceInfoList.stream().collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));
        QueryWrapper<InterfaceInfo> queryWrapper=new QueryWrapper<>();
        queryWrapper.in("id", idGroupMap.keySet());
        List<InterfaceInfo> list  = interfaceInfoService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list )) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        List<InterfaceInfoVo> interfaceInfoVoList = BeanCopyUtils.copyBeanList(list , InterfaceInfoVo.class);
        List<InterfaceInfoVo> interfaceInfoVos = interfaceInfoVoList.stream().map(interfaceInfoVo -> {
            List<UserInterfaceInfo> userInterfaceInfos = idGroupMap.get(interfaceInfoVo.getId());
            interfaceInfoVo.setTotalNum(userInterfaceInfos.get(0).getTotalNum() - userInterfaceInfos.get(0).getLeftNum());
            return interfaceInfoVo;
        }).collect(Collectors.toList());
        return ResultUtils.success(interfaceInfoVos);
    }

}
