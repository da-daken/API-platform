package com.daken.project.listener;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.daken.common.constants.RedisConstant;
import com.daken.common.entity.ApiOrder;
import com.daken.common.entity.UserInterfaceInfo;
import com.daken.project.constant.CommonConstant;
import com.daken.project.service.ApiOrderService;
import com.daken.project.service.UserInterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@RocketMQMessageListener(topic = "${daken.api.order.topic}",
        consumerGroup = "${daken.rocketmq.consumer.group}" )
@Slf4j
public class OrderPaySuccessListener implements RocketMQListener<String> {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private ApiOrderService apiOrderService;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    /**
     * 接收支付订单消息
     *
     * @param orderSn
     */
    @Override
    public void onMessage(String orderSn) {
        if (StrUtil.isBlank(orderSn)) {
            return;
        }
        try {
            String replace = orderSn.replace("\"", "");
            //消息抵达队列后，就进行删除操作
            redisTemplate.delete(RedisConstant.ORDER_PAY_SUCCESS_INFO + replace);
            // 解决重复投递问题
            Object o = redisTemplate.opsForValue().get(RedisConstant.ORDER_PAY_ROCKETMQ + replace);
            if (null == o) {
                log.info("监听到《订单支付成功》的消息：{}", replace);
                apiOrderService.orderPaySuccess(replace);
                ApiOrder order = apiOrderService.getOne(new QueryWrapper<ApiOrder>().eq("orderSn", replace));
                LambdaQueryWrapper<UserInterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(UserInterfaceInfo::getUserId, order.getUserId());
                queryWrapper.eq(UserInterfaceInfo::getInterfaceInfoId, order.getInterfaceId());
                UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(queryWrapper);
                if (Objects.isNull(userInterfaceInfo)) {
                    userInterfaceInfo = UserInterfaceInfo.builder()
                            .interfaceInfoId(order.getInterfaceId())
                            .userId(order.getUserId())
                            .leftNum(Math.toIntExact(order.getOrderNum()))
                            .totalNum(Math.toIntExact(order.getOrderNum()))
                            .status(CommonConstant.ZERO).build();
                    userInterfaceInfoService.save(userInterfaceInfo);
                } else {
                    userInterfaceInfo.setTotalNum(userInterfaceInfo.getTotalNum() + Math.toIntExact(order.getOrderNum()));
                    userInterfaceInfo.setLeftNum(userInterfaceInfo.getLeftNum() + Math.toIntExact(order.getOrderNum()));
                    userInterfaceInfoService.updateById(userInterfaceInfo);
                }
                redisTemplate.opsForValue().set(RedisConstant.ORDER_PAY_ROCKETMQ + replace, "true", 30, TimeUnit.MINUTES);
            }
        } catch (Exception e){
            redisTemplate.delete(RedisConstant.ORDER_PAY_ROCKETMQ +orderSn.replace("\"", ""));
        }
    }

}
