package com.daken.project.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAkSkVo {
    /**
     * userId
     */
    private Long id;
    /**
     * ak
     */
    private String accessKey;
    /**
     * sk
     */
    private String secretKey;
}
