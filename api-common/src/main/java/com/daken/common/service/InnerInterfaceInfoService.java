package com.daken.common.service;

import com.daken.common.entity.InterfaceInfo;
import com.daken.common.entity.Router;

public interface InnerInterfaceInfoService {
    /**
     * 从数据库中查询模拟接口是否存在（请求路径、请求方法、请求参数）
     */
    InterfaceInfo getInterfaceInfo(String url, String method);


}
