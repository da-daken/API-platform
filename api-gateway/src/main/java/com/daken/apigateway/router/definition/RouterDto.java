package com.daken.apigateway.router.definition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouterDto implements Serializable {
    private String filters;

    private String predicates;

    private String url;

    private Integer orderId;

    private String routerId;
}
