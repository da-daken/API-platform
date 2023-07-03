package com.daken.project.model.dto.interfaceinfo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 创建请求
 *
 * @TableName product
 */
@Data
public class InterfaceInfoAddRequest implements Serializable {

    //名称
    private String name;
    //描述
    private String description;
    //接口地址
    private String url;
    //请求头
    private String requestHeader;
    //响应头
    private String responseHeader;
    //请求类型
    private String method;
    //请求参数
    private String requestParams;
    //接口服务器地址
    private String host;

}