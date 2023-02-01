package com.daken.apigateway.router.runner;

import com.daken.apigateway.router.definition.MyFilterDefinition;
import com.daken.apigateway.router.definition.MyPredicateDefinition;
import com.daken.apigateway.router.definition.MyRouteDefinition;
import com.daken.apigateway.router.definition.RouterDto;
import com.daken.apigateway.router.impl.DynamicRouteServiceImpl;
import com.daken.apigateway.router.util.BeanCopyUtils;
import com.daken.apigateway.router.util.JsonUtils;
import com.daken.common.entity.Router;
import com.daken.common.service.InnerRouterService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 开机自动读取接口的路由到内存中
 */
@Component
public class RouterRunner implements CommandLineRunner {

    @Resource
    private DynamicRouteServiceImpl routeService;

    @DubboReference
    private InnerRouterService innerRouterService;

    @Override
    public void run(String... args) throws Exception {
        // 1. 远程调用数据库查出所有的路由信息
        List<Router> allRouters = innerRouterService.getAllRouters();
        // 2. 对路由信息进行 dto 封装
        List<RouterDto> routerDtos = BeanCopyUtils.copyBeanList(allRouters, RouterDto.class);
        for (RouterDto router : routerDtos) {
            // 3. 将其他参数直接设置进去
            MyRouteDefinition myRouteDefinition = new MyRouteDefinition();
            myRouteDefinition.setRouterId(router.getRouterId());
            myRouteDefinition.setUrl(router.getUrl());
            myRouteDefinition.setOrderId(router.getOrderId());
            // 4. 将里面的 filters, predicates 转成 list<> 封装到 MyRouterDefinition 里面
            List<MyFilterDefinition> myFilterDefinitions = JsonUtils.getJsonToList(router.getFilters(), MyFilterDefinition.class);
            List<MyPredicateDefinition> myPredicateDefinitions = JsonUtils.getJsonToList(router.getPredicates(), MyPredicateDefinition.class);
            if(myFilterDefinitions != null) {
                myRouteDefinition.setFilters(myFilterDefinitions);
            }
            if(myPredicateDefinitions != null) {
                myRouteDefinition.setPredicates(myPredicateDefinitions);
            }
            // 5. 把路由加进来
            routeService.add(myRouteDefinition);
        }
    }
}
