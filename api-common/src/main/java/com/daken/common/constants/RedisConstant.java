package com.daken.common.constants;

/**
 * redis 的常量
 *
 * @author daken
 */
public class RedisConstant {
    public static final String onlinePageCacheKey = "api:onlinePage:";

    public static final String SEND_ORDER_PREFIX = "api:order:sendOrderSnInfo:";

    public static final String ORDER_PAY_SUCCESS_INFO = "api:order:paySuccess:";

    public static final String ORDER_PAY_ROCKETMQ = "api:order:payRocketmq:";

    public static final String PAY_TRADE_INFO = "api:order:payInfo:";
}
