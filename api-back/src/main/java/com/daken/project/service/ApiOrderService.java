package com.daken.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.daken.common.BaseResponse;
import com.daken.common.entity.ApiOrder;
import com.daken.project.model.dto.order.ApiOrderCancelDto;
import com.daken.project.model.dto.order.ApiOrderDto;
import com.daken.project.model.dto.order.ApiPayDto;
import com.daken.project.model.vo.order.OrderSnVo;
import com.daken.project.model.dto.order.ApiOrderStatusInfoDto;
import com.daken.project.model.vo.order.ApiOrderStatusVo;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
* @author daken
* @description 针对表【order】的数据库操作Service
*/
public interface ApiOrderService extends IService<ApiOrder> {

    /**
     * 生成订单接口
     * @param apiOrderDto
     * @param request
     * @param response
     * @return
     */
    BaseResponse<OrderSnVo> generateOrderSn(ApiOrderDto apiOrderDto, HttpServletRequest request, HttpServletResponse response) throws ExecutionException, InterruptedException;

    /**
     * 生成防重令牌：保证创建订单的接口幂等性
     * @param id
     * @param response
     * @return
     */
    BaseResponse generateToken(Long id,HttpServletResponse response);

    /**
     * 取消订单
     * @param apiOrderCancelDto
     * @param request
     * @param response
     * @return
     */
    BaseResponse cancelOrderSn(ApiOrderCancelDto apiOrderCancelDto, HttpServletRequest request, HttpServletResponse response);

    /**
     * 扣减库存相关操作
     * @param orderSn
     */
    void orderPaySuccess(String orderSn);

    /**
     * 获取当前登录用户的status订单信息
     * @param statusInfoDto
     * @param request
     * @return
     */
    BaseResponse<Page<ApiOrderStatusVo>> getCurrentOrderInfo(ApiOrderStatusInfoDto statusInfoDto, HttpServletRequest request);

    /**
     * 获取echarts图中最近7天的交易数
     * @param dateList
     * @return
     */
    BaseResponse getOrderEchartsData(List<String> dateList);

    /**
     * 支付订单
     * @param payDto
     * @param request
     * @return
     */
    BaseResponse payOrder(ApiPayDto payDto, HttpServletRequest request);
}
