package com.daken.common.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接口信息(InterfaceInfo)表实体类
 *
 * @author makejava
 * @since 2022-11-16 13:54:14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "interface_info")
public class InterfaceInfo implements Serializable {
    //主键
    @TableId(type = IdType.AUTO)
    private Long id;
    //名称
    private String name;
    //描述
    private String description;
    //接口路径 （需要唯一）
    private String url;
    //接口服务器地址
    private String host;
    //请求头
    private String requestHeader;
    //请求参数
    private String requestParams;
    //响应头
    private String responseHeader;
    //接口状态（0-关闭，1-开启）
    private Integer status;
    //请求类型
    private String method;
    //创建人
    private Long userId;
    //参数的全类名
    private String paramsType;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;
    //是否删除(0-未删, 1-已删)
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}

