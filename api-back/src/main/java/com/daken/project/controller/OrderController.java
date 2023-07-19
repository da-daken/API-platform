package com.daken.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daken.common.entity.ApiOrder;
import com.daken.project.model.dto.order.ApiOrderCancelDto;
import com.daken.project.model.dto.order.ApiOrderDto;
import com.daken.project.model.dto.order.ApiOrderStatusInfoDto;
import com.daken.project.model.dto.order.ApiPayDto;
import com.daken.project.model.vo.order.ApiOrderStatusVo;
import com.daken.project.model.vo.order.OrderSnVo;
import com.daken.project.service.ApiOrderService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.daken.common.BaseResponse;
import com.daken.common.ErrorCode;
import com.daken.common.ResultUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author daken
 */
@RestController
@Api("订单接口")
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private ApiOrderService apiOrderService;


    /**
     * 获取echarts图中最近7天的交易数
     * @return
     */
    @PostMapping("/getOrderEchartsData")
    public BaseResponse getOrderEchartsData(@RequestBody List<String> dateList){
        return apiOrderService.getOrderEchartsData(dateList);
    }


    /**
     * 获取全站成功交易数
     * @return
     */
    @GetMapping("/getSuccessOrder")
    public BaseResponse getSuccessOrder(){
        return ResultUtils.success(apiOrderService.count(new QueryWrapper<ApiOrder>().eq("status",1)));
    }

    /**
     * 获取当前登录用户的status订单信息
     * @param statusInfoDto
     * @param request
     * @return
     */
    @PostMapping("/getCurrentOrderInfo")
    public BaseResponse<Page<ApiOrderStatusVo>> getCurrentOrderInfo(@RequestBody ApiOrderStatusInfoDto statusInfoDto, HttpServletRequest request){
        return apiOrderService.getCurrentOrderInfo(statusInfoDto,request);
    }

    /**
     * 生成防重令牌：保证创建订单的接口幂等性
     * @param id
     * @param response
     * @return
     */
    @GetMapping("/generateToken")
    public BaseResponse generateToken(Long id,HttpServletResponse response){
        return apiOrderService.generateToken(id,response);
    }


    /**
     * 创建订单
     * @param apiOrderDto
     * @return
     */
    @PostMapping("/generateOrderSn")
    public BaseResponse<OrderSnVo> generateOrderSn(@RequestBody ApiOrderDto apiOrderDto, HttpServletRequest request, HttpServletResponse response) throws ExecutionException, InterruptedException {
        return apiOrderService.generateOrderSn(apiOrderDto,request,response);
    }


    /**
     * 取消订单
     * @param apiOrderCancelDto
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/cancelOrderSn")
    public BaseResponse cancelOrderSn(@RequestBody ApiOrderCancelDto apiOrderCancelDto, HttpServletRequest request, HttpServletResponse response) {
        return apiOrderService.cancelOrderSn(apiOrderCancelDto,request,response);
    }

    /**
     * 支付订单
     * @param payDto
     * @return
     */
    @PostMapping("/pay")
    public synchronized BaseResponse payOrder(@RequestBody ApiPayDto payDto, HttpServletRequest request){
        return apiOrderService.payOrder(payDto, request);
    }
}
