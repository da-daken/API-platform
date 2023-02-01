package com.daken.apiinterface.filter;

import lombok.val;
import org.apache.catalina.filters.ExpiresFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class GatewayFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String gateway=request.getHeader("gateWay");
        if(!"123".equals(gateway)){
            response.setContentType("application/json; charset=utf-8");
            val writer = response.getWriter();
            writer.write("不可以直接访问哟小宝贝");
            return ;
        }
        filterChain.doFilter(request, response);
    }
}
