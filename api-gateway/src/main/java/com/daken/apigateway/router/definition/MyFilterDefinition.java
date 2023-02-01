package com.daken.apigateway.router.definition;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class MyFilterDefinition {

    private String name;

    private Map<String,String> args = new HashMap<>();

}
