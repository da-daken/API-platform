package com.daken.project.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户与接口之间的信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SelfInterfaceDataVo {
    /**
     * 接口名称
     */
    private String name;
    /**
     * 总调用次数
     */
    private Integer totalNum;
    /**
     * 剩余调用次数
     */
    private Integer leftNum;
}
