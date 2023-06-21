package com.daken.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * KeyResolver用于计算某一个类型的限流的KEY也就是说，可以通过KeyResolver来指定限流的Key
 */
@Configuration
public class IpKeyResolver implements KeyResolver {

    /**
     * 1.返回值Mono<String> 泛型中的String 就代表令牌分给谁
     * @param exchange
     * @return
     */
    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        //根据ip限流
        String hostAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        return Mono.just(hostAddress);
    }
}
