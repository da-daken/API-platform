package com.daken.project.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daken.common.BaseResponse;
import com.daken.common.ErrorCode;
import com.daken.common.ResultUtils;
import com.daken.common.constants.CookieConstant;
import com.daken.common.constants.OrderConstant;
import com.daken.common.constants.RedisConstant;
import com.daken.common.entity.ApiOrder;
import com.daken.common.entity.InterfaceInfo;
import com.daken.common.entity.User;
import com.daken.common.entity.UserInterfaceInfo;
import com.daken.project.constant.CommonConstant;
import com.daken.project.constant.OrderStatus;
import com.daken.project.exception.BusinessException;
import com.daken.project.mapper.ApiOrderMapper;
import com.daken.project.model.dto.order.ApiOrderCancelDto;
import com.daken.project.model.dto.order.ApiOrderDto;
import com.daken.project.model.dto.order.ApiOrderStatusInfoDto;
import com.daken.project.model.dto.order.ApiPayDto;
import com.daken.project.model.vo.order.ApiOrderStatusVo;
import com.daken.project.model.vo.order.OrderSnVo;
import com.daken.project.service.ApiOrderService;
import com.daken.project.model.vo.order.EchartsVo;
import com.daken.project.service.InterfaceInfoService;
import com.daken.project.service.UserInterfaceInfoService;
import com.daken.project.service.UserService;
import com.daken.project.utils.RocketMqUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author 12866
* @description 针对表【order】的数据库操作Service实现
* @createDate 2023-03-14 17:17:35
*/
@Service
public class ApiOrderServiceImpl extends ServiceImpl<ApiOrderMapper, ApiOrder>
    implements ApiOrderService {


    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private UserService userService;

    @Resource
    private ApiOrderMapper apiOrderMapper;

    @Resource
    private RocketMqUtils rocketMqUtils;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private Snowflake snowflake;

    /**
     * 生成订单
     * @param apiOrderDto
     * @param request
     * @param response
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public BaseResponse<OrderSnVo> generateOrderSn(@RequestBody ApiOrderDto apiOrderDto, HttpServletRequest request, HttpServletResponse response) throws ExecutionException, InterruptedException {
        //1、远程获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        if (null == loginUser){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        //2、健壮性校验
        Long userId = apiOrderDto.getUserId();
        Double totalAmount = apiOrderDto.getTotalAmount();
        Long orderNum = apiOrderDto.getOrderNum();
        Double charging = apiOrderDto.getCharging();
        Long interfaceId = apiOrderDto.getInterfaceId();
        if (null == userId || null==totalAmount || null == orderNum || null ==charging || null==interfaceId){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (!userId.equals(loginUser.getId())){
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceId);
        if (null == interfaceInfo){
            throw new BusinessException(ErrorCode.INTERFACE_NO_FOUND);
        }
        //保留两位小数
        Double temp = orderNum * charging;
        BigDecimal two = new BigDecimal(temp);
        Double three = two.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (!three.equals(totalAmount)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        //3、验证令牌是否合法【令牌的对比和删除必须保证原子性】
        Cookie[] cookies = request.getCookies();
        String token = null;
        for (Cookie cookie : cookies) {
            if (CookieConstant.orderToken.equals(cookie.getName())){
                token = cookie.getValue();
            }
        }
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long result = (Long) redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + loginUser.getId()),
                token);
        if (result == 0L){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"提交太快了，请重新提交");
        }
        //5、使用雪花算法生成订单id，并保存订单
        String orderSn = generateOrderSn(loginUser.getId().toString());
        ApiOrder apiOrder = new ApiOrder();
        apiOrder.setTotalAmount(totalAmount);
        apiOrder.setOrderSn(orderSn);
        apiOrder.setOrderNum(orderNum);
        apiOrder.setStatus(OrderStatus.ORDER_NO_PAY.getStatus());
        apiOrder.setInterfaceId(interfaceId);
        apiOrder.setUserId(userId);
        apiOrder.setCharging(charging);
        try {
            apiOrderMapper.insert(apiOrder);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"订单保存失败");
        }
        //6、锁定剩余库存
        //7、远程更新剩余可调用接口数量
        //8、全部锁定完成后，向mq延时队列发送订单消息，且30分钟过期
        rocketMqUtils.sendOrderSnInfo(apiOrder);
        //9、构建返回给前端页面的数据
        OrderSnVo orderSnVo = new OrderSnVo();
        BeanUtils.copyProperties(apiOrder,orderSnVo);
        DateTime date = DateUtil.date();
        orderSnVo.setCreateTime(date);
        orderSnVo.setExpirationTime(DateUtil.offset(date, DateField.MINUTE,30));
        orderSnVo.setName(interfaceInfo.getName());
        orderSnVo.setDescription(interfaceInfo.getDescription());
        return ResultUtils.success(orderSnVo);
    }

    /**
     * 生成防重令牌：保证创建订单的接口幂等性
     * @param id
     * @param response
     * @return
     */
    @Override
        public BaseResponse generateToken(Long id,HttpServletResponse response) {
        if (null == id){
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR);
        }
        //防重令牌
        String token = IdUtil.simpleUUID();
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + id,token,30, TimeUnit.MINUTES);
        Cookie cookie = new Cookie(CookieConstant.orderToken,token);
        cookie.setPath("/");
        cookie.setMaxAge(CookieConstant.orderTokenExpireTime);
        response.addCookie(cookie);
        return ResultUtils.success(null);
    }

    /**
     * 取消订单
     * @param apiOrderCancelDto
     * @param request
     * @param response
     * @return
     */
    @Override
    public BaseResponse cancelOrderSn(ApiOrderCancelDto apiOrderCancelDto, HttpServletRequest request, HttpServletResponse response) {
        Long orderNum = apiOrderCancelDto.getOrderNum();
        String orderSn = apiOrderCancelDto.getOrderSn();
        //订单已经被取消的情况
        ApiOrder orderSn1 = this.getOne(new QueryWrapper<ApiOrder>().eq("orderSn", orderSn));
        if (Objects.equals(orderSn1.getStatus(), OrderStatus.ORDER_CANCEL.getStatus())){
            return ResultUtils.success("取消订单成功");
        }
        //更新订单表状态
        this.update(new UpdateWrapper<ApiOrder>().eq("orderSn", orderSn).set("status",OrderStatus.ORDER_CANCEL));
        return ResultUtils.success("取消订单成功");
    }

    /**
     * 扣减库存相关操作
     * @param orderSn
     */
    @Override
    public void orderPaySuccess(String orderSn) {
        this.update(new UpdateWrapper<ApiOrder>().eq("orderSn",orderSn).set("status",OrderStatus.ORDER_PAY_SUCCESS.getStatus()));
    }

    /**
     * 获取当前登录用户的status订单信息
     * @param statusInfoDto
     * @param request
     * @return
     */
    @Override
    public BaseResponse<Page<ApiOrderStatusVo>> getCurrentOrderInfo(ApiOrderStatusInfoDto statusInfoDto, HttpServletRequest request) {
        Long userId = statusInfoDto.getUserId();
        //前端筛选即可
        Integer status = statusInfoDto.getStatus();
        if (null == userId){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = statusInfoDto.getCurrent();
        // 限制爬虫
        long size = statusInfoDto.getPageSize();
        if (size > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<ApiOrderStatusVo> apiOrderStatusVo = apiOrderMapper.getCurrentOrderInfo(new Page<>(current, size),userId,status);
        List<ApiOrderStatusVo> records = apiOrderStatusVo.getRecords();
        List<ApiOrderStatusVo> collect = records.stream().map(record -> {
            Integer status1 = record.getStatus();
            if (status1 == 0) {
                Date date = record.getCreateTime();
                record.setExpirationTime(DateUtil.offset(date, DateField.MINUTE, 30));
            }
            return record;
        }).collect(Collectors.toList());
        apiOrderStatusVo.setRecords(collect);
        return ResultUtils.success(apiOrderStatusVo);
    }

    /**
     * 获取echarts图中最近7天的交易数
     * @param dateList
     * @return
     */
    @Override
    public BaseResponse getOrderEchartsData(List<String> dateList) {
        List<EchartsVo> list=apiOrderMapper.getOrderEchartsData(dateList);
        return ResultUtils.success(list);
    }

    /**
     * 支付订单 ( todo 可以做些支付的第三方接口，例：支付宝，微信)
     * 现在无面向用户的需求，先简单进行数据库修改
     *
     * @param payDto
     * @param request
     * @return
     */
    @Override
    @Transactional
    public BaseResponse payOrder(ApiPayDto payDto, HttpServletRequest request) {
        // 1. 获取当前user
        User loginUser = userService.getLoginUser(request);
        if (null == loginUser){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 2. 健壮性校验 (接口是否存在，用户是否是同一个，订单是否被取消)
        Long interfaceInfoId = payDto.getInterfaceInfoId();
        Long userId = payDto.getUserId();
        Long orderNum = payDto.getOrderNum();
        String orderSn = payDto.getOrderSn();
        Integer status = payDto.getStatus();
        if (null == interfaceInfoId || null == orderNum || null == userId || null == orderSn || null == status){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (!userId.equals(loginUser.getId())){
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceInfoId);
        if (null == interfaceInfo){
            throw new BusinessException(ErrorCode.INTERFACE_NO_FOUND);
        }
        if (OrderStatus.ORDER_CANCEL.getStatus().equals(status)){
            return ResultUtils.error(ErrorCode.ORDER_BE_CANCEL);
        }
        // 3. 购买接口,修改订单状态
        // 幂等性保证：判断该订单是否被处理过
        Object o = redisTemplate.opsForValue().get(RedisConstant.PAY_TRADE_INFO + orderSn);
        if (null == o) {
            // 购买成功，修改订单状态
            redisTemplate.opsForValue().set(RedisConstant.PAY_TRADE_INFO + orderSn, orderSn);
            rocketMqUtils.sendOrderPaySuccessInfo(orderSn);
            return ResultUtils.success("购买成功");
        }
        return ResultUtils.error(ErrorCode.OPERATION_ERROR, "订单正在处理或者已经处理完成，请稍后。。");
    }


    /**
     * 生成订单号
     * @return
     */
    private String generateOrderSn(String userId){
        String timeId = IdWorker.getTimeId();
        String substring = timeId.substring(0, timeId.length() - 15);
        return substring + RandomUtil.randomNumbers(5) + userId.substring(userId.length()-2,userId.length());
    }
}




