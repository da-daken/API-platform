package com.daken.apigateway.router.definition;

import lombok.Data;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Data
public class MyRouteDefinition {
    private String routerId;

    private List<MyFilterDefinition> filters = new ArrayList<>();

    private List<MyPredicateDefinition> predicates = new ArrayList<>();

    private String url;

    private int orderId = 0;

    public RouteDefinition getRouteDefinition(){
        RouteDefinition definition = new RouteDefinition();
        definition.setId(this.getRouterId());
        definition.setOrder(this.getOrderId());

        //设置断言
        List<PredicateDefinition> pdList = new ArrayList<>();
        List<MyPredicateDefinition> myPredicateDefinitionList = this.getPredicates();
        for (MyPredicateDefinition gpDefinition: myPredicateDefinitionList) {
            PredicateDefinition predicate = new PredicateDefinition();
            predicate.setArgs(gpDefinition.getArgs());
            predicate.setName(gpDefinition.getName());
            pdList.add(predicate);
        }

        definition.setPredicates(pdList);

        // 设置过滤器(可以用于令牌桶限流)
        List<FilterDefinition> filters = new ArrayList();
        List<MyFilterDefinition> gatewayFilters = this.getFilters();
        for(MyFilterDefinition filterDefinition : gatewayFilters){
            FilterDefinition filter = new FilterDefinition();
            filter.setName(filterDefinition.getName());
            filter.setArgs(filterDefinition.getArgs());
            filters.add(filter);
        }
        definition.setFilters(filters);

        URI url = null;
        if(this.getUrl().startsWith("http")){
            url = UriComponentsBuilder.fromHttpUrl(this.getUrl()).build().toUri();
        }else{
            url = URI.create(this.getUrl());
        }
        definition.setUri(url);
        return definition;
    }
}
