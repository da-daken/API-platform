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
     * 用户id，用于标识
     */
    private Long userId;

    /**
     * 接口id，用于标识
     */
    private Long interfaceInfoId;

    /**
     * 需要购买（增加）的接口数量
     */
    private Integer leftNum;

}
