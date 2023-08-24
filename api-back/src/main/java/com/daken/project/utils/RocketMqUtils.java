package com.daken.project.utils;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.daken.common.constants.RedisConstant;
import com.daken.common.entity.ApiOrder;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;

/**
 * RocketMq 发送消息 向延时队列中发送消息
 *
 * @author daken
 */
public class RocketMqUtils {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private RedisCache redisCache;

    @Value("${daken.api.order.topic}")
    private String topic;

    private Long DELAY_TIME = 30 * 60 * 1000L;

    private Long finalId1 = null;

    private String finalId2 = null;

    /**
     * 发送订单消息,延时队列30分钟，未支付会自动过期
     *
     * @param apiOrder
     */
    public void sendOrderSnInfo(ApiOrder apiOrder){
        finalId1 = apiOrder.getId();
        redisCache.setCacheObject(RedisConstant.SEND_ORDER_PREFIX + apiOrder.getId(), apiOrder);
        String message = JSON.toJSONString(apiOrder, new SerializerFeature[]{SerializerFeature.WriteClassName});
        // 延时队列
        rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload(message).build(), DELAY_TIME);
    }

    /**
     * 发送支付成功的消息
     * @param orderSn
     */
    public SendStatus sendOrderPaySuccessInfo(String orderSn){
        finalId2 = orderSn;
        redisCache.setCacheObject(RedisConstant.ORDER_PAY_SUCCESS_INFO + orderSn, orderSn);
        String message = JSON.toJSONString(orderSn, new SerializerFeature[]{SerializerFeature.WriteClassName});
        SendResult sendResult = rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload(message).build());
        return sendResult.getSendStatus();
    }
}
