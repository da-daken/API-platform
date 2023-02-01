package com.daken.common.service;

import com.daken.common.entity.Router;

import java.util.List;

public interface InnerRouterService {

    /**
     * 获取全部的路由信息
     * @return
     */
    List<Router> getAllRouters();
}
