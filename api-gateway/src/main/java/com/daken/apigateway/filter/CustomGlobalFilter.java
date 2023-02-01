package com.daken.apigateway.filter;

import com.daken.apiclientsdk.utils.SignUtils;
import com.daken.common.entity.InterfaceInfo;
import com.daken.common.entity.User;
import com.daken.common.service.InnerInterfaceInfoService;
import com.daken.common.service.InnerUserInterfaceInfoService;
import com.daken.common.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 *  全局过滤
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

//    public static final String INTERFACE_HOST = "http://localhost:8123";

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 用户发送请求到API网关(到这就已经实现了)
        // 2. 请求日志
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getPath().value();
        String method = request.getMethod().toString();
        String sourceAddress = request.getRemoteAddress().getAddress().getHostAddress();
        log.info("请求唯一标识"+request.getId());
        log.info("请求路径"+ url);
        log.info("请求方法"+ method);
        log.info("请求参数"+ request.getQueryParams().toSingleValueMap());
        log.info("请求来源地址"+sourceAddress);
        ServerHttpResponse response = exchange.getResponse();
        // 3. 黑白名单
        if(!IP_WHITE_LIST.contains(sourceAddress)){
            return handleNoAuth(response);
        }
        // 4. 用户鉴权（判断ak，sk是否合法）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = null;
        try {
            body = URLDecoder.decode(headers.getFirst("body"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        // 去数据库中查是否已分配给用户
//        if (!"api".equals(accessKey)) {
//            return handleNoAuth(response);
//        }
        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("getInvokeUser error" , e);
        }
        if(invokeUser == null){
            return handleNoAuth(response);
        }

        if (Long.parseLong(nonce) > 10000) {
            return handleNoAuth(response);
        }
        // 不能超过5分钟
        Long currentTime=System.currentTimeMillis()/1000;
        Long FIVE_MINUTES=60*5L;
        if((currentTime-Long.parseLong(timestamp)) >= FIVE_MINUTES){
            return handleNoAuth(response);
        }

        String serverSign = SignUtils.genSign(body, invokeUser.getSecretKey());
        if (!sign.equals(serverSign)) {
            return handleNoAuth(response);
        }
        // 5. 请求的模拟接口是否存在(从数据库种查询接口是否存在)
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(url, method);
        } catch (Exception e) {
            log.error("getInterfaceInfo error" , e);
        }
        if(interfaceInfo == null){
            return handleNoAuth(response);
        }
        // 6. 将请求加上特定的请求头，表示是从gateway转发的(调用成功后，加入。在下面的方法里实现了)

        // 7. 查询用户是否还有调用次数
        if(!innerUserInterfaceInfoService.isLeftnum(interfaceInfo.getId(), invokeUser.getId())){
            return handleNoAuth(response);
        }

        // 8. 请求转发，调用模拟接口
//        Mono<Void> filter = chain.filter(exchange);
//        log.info("响应："+ response.getStatusCode());
        // 9. 响应日志
        return handleResponse(exchange, chain, interfaceInfo.getId(), invokeUser.getId()) ;
    }

    /**
     *  处理响应
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId){
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();

            // 将请求加上特定的请求头，表示是从gateway转发的
            ServerHttpRequest req = exchange.getRequest().mutate()
                    .header("gateWay", "123").build();

            if(statusCode == HttpStatus.OK){
                // 增强能力,在调用完接口后 再执行下列增强方法
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        //log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            return super.writeWith(
                                 fluxBody.map(dataBuffer -> {
                                    // 调用成功，接口调用次数 + 1
                                     try {
                                         boolean invokeCount = innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                                     } catch (Exception e) {
                                         log.error("invokeCount error", e);
                                     }
                                     byte[] content = new byte[dataBuffer.readableByteCount()];
                                    dataBuffer.read(content);
                                    DataBufferUtils.release(dataBuffer);//释放掉内存
                                    // 构建日志
                                    StringBuilder sb2 = new StringBuilder(200);
                                    List<Object> rspArgs = new ArrayList<>();
                                    rspArgs.add(originalResponse.getStatusCode());
                                    String data = new String(content, StandardCharsets.UTF_8);//data
                                    sb2.append(data);
                                    // 打印日志
                                    log.info("响应结果:" + data);
                                    return bufferFactory.wrap(content);
                                })
                            );
                        } else {
                            // 9. 调用失败，返回一个规范的错误码
                            handleInvokeError(originalResponse);
                            log.error("<--- {} 响应code异常", getStatusCode());

                        }
                        return super.writeWith(body);
                    }
                };
                return chain.filter(exchange.mutate().response(decoratedResponse)
                                .request(req.mutate().build())
                        .build());
            }
            return chain.filter(exchange);//降级处理返回数据
        }catch (Exception e){
            log.error("gateway log exception.\n" + e);
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    /**
     * 返回权限不够
     * @param response
     * @return
     */
    public Mono<Void> handleNoAuth(ServerHttpResponse response){
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    /**
     * 返回调用失败
     * @param response
     * @return
     */
    public Mono<Void> handleInvokeError(ServerHttpResponse response){
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
}