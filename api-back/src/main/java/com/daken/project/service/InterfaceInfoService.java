package com.daken.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.daken.common.entity.InterfaceInfo;


/**
 * 接口信息(InterfaceInfo)表服务接口
 *
 * @author makejava
 * @since 2022-11-16 13:54:15
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {
    void validInterfaceInfo(InterfaceInfo interfaceInfo,boolean add);
}

