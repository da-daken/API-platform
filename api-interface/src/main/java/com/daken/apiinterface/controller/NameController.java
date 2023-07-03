package com.daken.apiinterface.controller;

import com.daken.apiclientsdk.model.User;
import com.daken.apiinterface.common.BaseResponse;
import com.daken.apiinterface.common.ResultUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/name")
public class NameController {


    @GetMapping("/get")
    public BaseResponse<String> getNameByGet(String name) {
        String res = "GET你的名字是：" + name;
        return ResultUtils.success(res);
    }


    @PostMapping("/user")
    public String getUsernameByPost(@RequestBody User user, HttpServletRequest request) {
        return "POST 用户名字是" + user.getUsername();
    }

}
