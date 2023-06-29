package com.daken.project.model.dto.userInterfaceInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserInterfaceInfoBuyDto implements Serializable {

    /**
     * 接口名称，用于标识
     */
    private String interfaceName;

    /**
     * 用户名称，用于标识
     */
    private String payAccount;

    /**
     * 需要购买（增加）的接口数量
     */
    private Integer num;

}
