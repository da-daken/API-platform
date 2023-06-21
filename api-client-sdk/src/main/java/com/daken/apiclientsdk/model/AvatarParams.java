package com.daken.apiclientsdk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 获取随机头像参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvatarParams implements Serializable {
    private String form;

    private static final long serialVersionUID = 1L;

}
