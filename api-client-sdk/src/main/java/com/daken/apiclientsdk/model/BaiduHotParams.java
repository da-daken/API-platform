package com.daken.apiclientsdk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 百度热搜参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaiduHotParams implements Serializable {
    private Integer size;

    private static final long serialVersionUID = 1L;
}
