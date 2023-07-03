package com.daken.apiinterface.controller;

import cn.hutool.http.HttpUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/douyin")
public class DouYinController {

    @GetMapping("/parse")
    public String parseDouYinUrl(String url){
        String parseUrl = "https://api:btstu.cn/ayjx/api.php";
        HashMap<String, Object> params = new HashMap<>();
        params.put("url", url);
        String result = HttpUtil.get(parseUrl, params);
        return result;
    }
}
