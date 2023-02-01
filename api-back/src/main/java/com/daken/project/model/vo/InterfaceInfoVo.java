package com.daken.project.model.vo;

import com.daken.common.entity.InterfaceInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * 接口调用次数信息封装 VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InterfaceInfoVo extends InterfaceInfo implements Serializable {

    /**
     * 总共的调用次数
     */
    private Integer totalNum;

    private static final long serialVersionUID = 1L;
}
