package com.daken.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.daken.common.entity.UserInterfaceInfo;


/**
 * 用户调用接口关系(UserInterfaceInfo)表服务接口
 *
 * @author makejava
 * @since 2022-11-23 11:06:28
 */
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);
}

