package com.daken.project.listener;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.daken.common.ErrorCode;
import com.daken.common.constants.RedisConstant;
import com.daken.common.entity.ApiOrder;
import com.daken.project.constant.LockPrefix;
import com.daken.project.constant.OrderStatus;
import com.daken.project.exception.BusinessException;
import com.daken.project.service.ApiOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
@RocketMQMessageListener(topic = "${daken.api.order.delay.topic}",
        consumerGroup = "${daken.rocketmq.consumer.delay.group}" )
@Slf4j
public class OrderDelayListener implements RocketMQListener<String> {

    @Resource
    private ApiOrderService apiOrderService;

    @Resource
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 接收延时队列的消息
     * 1. order 状态还未支付将订单取消
     * 2. order 状态已经支付就放行
     * @param s
     */
    @Override
    public void onMessage(String s) {
        if (StrUtil.isBlank(s)){
            return ;
        }
        ApiOrder apiOrder = JSON.parseObject(s, ApiOrder.class);
        // 1. 检查 order 的状态
        LambdaQueryWrapper<ApiOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApiOrder::getOrderSn, apiOrder.getOrderSn());
        ApiOrder one = apiOrderService.getOne(wrapper);
        Integer status = one.getStatus();
        if (null == status){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        RLock lock = redissonClient.getLock(LockPrefix.PAY_LOCK_PREFIX + apiOrder.getOrderSn());
        try {
            if (lock.tryLock(80, -1, TimeUnit.SECONDS)){
                // 状态还未支付就取消
                if (OrderStatus.ORDER_NO_PAY.getStatus().equals(status)){
                    log.info("接收到还未支付订单：{} ,现将其自动取消", apiOrder.getOrderSn());
                    apiOrderService.update(new UpdateWrapper<ApiOrder>().eq("orderSn", one.getOrderSn()).set("status", OrderStatus.ORDER_CANCEL.getStatus()));
                }
                return ;
            }
            // 没获取锁就说明正在支付回调，不用取消该订单
        } catch (InterruptedException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        // 状态已支付就不管了
        redisTemplate.delete(RedisConstant.SEND_ORDER_PREFIX + apiOrder.getId());
    }
}
