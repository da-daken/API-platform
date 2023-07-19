package com.daken.project.model.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 购买的dto
 *
 * @author daken
 */
@Data
@AllArgsConstructor
public class ApiPayDto {
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 订单id
     */
    private Long interfaceInfoId;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 购买数量
     */
    private Long orderNum;

    /**
     * 交易状态【0->待付款；1->已完成；2->无效订单】
     */
    private Integer status;
}
