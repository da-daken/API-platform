package com.daken.project.model.dto.userInterfaceInfo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInterfaceInfoBuyDto implements Serializable {

    private Long userId;

    private Long interfaceInfoId;

    private Integer leftNum;

}
