package com.daken.project.service.impl.inner;

import com.daken.common.entity.Router;
import com.daken.common.service.InnerRouterService;
import com.daken.project.service.RouterService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class InnerRouterServiceImpl implements InnerRouterService {

    @Resource
    private RouterService routerService;

    @Override
    public List<Router> getAllRouters() {
        List<Router> routerList = routerService.list();
        return routerList;
    }
}
