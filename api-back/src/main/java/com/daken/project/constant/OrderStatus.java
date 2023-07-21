package com.daken.project.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    ORDER_PAY_SUCCESS(1, "订单成功支付"),
    ORDER_NO_PAY(0, "订单还未支付"),
    ORDER_CANCEL(2, "订单被取消");

    private final Integer status;

    private final String descrption;
}
