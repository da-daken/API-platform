package com.daken.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName router
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("router")
public class Router implements Serializable {
    /**
     * 
     */
    private Integer id;

    /**
     * 
     */
    private String routerId;

    /**
     * 
     */
    private String filters;

    /**
     * 
     */
    private String predicates;

    /**
     * 
     */
    private String url;

    /**
     * 
     */
    private Integer orderId;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}