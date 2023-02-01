package com.daken.project.model.dto.interfaceinfo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 更新请求
 *
 * @TableName product
 */
@Data
public class InterfaceInfoUpdateRequest implements Serializable {

    //主键
    private Long id;
    //名称
    private String name;
    //描述
    private String description;
    //接口地址
    private String url;
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
}