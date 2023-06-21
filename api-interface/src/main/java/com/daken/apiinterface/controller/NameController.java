package com.daken.apiinterface.controller;

import com.daken.apiclientsdk.model.User;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/name")
public class NameController {


    @GetMapping("/get")
    public String getNameByGet(String name) {
        return "GET 你的名字是" + name;
    }


    @PostMapping("/user")
    public String getUsernameByPost(@RequestBody User user, HttpServletRequest request) {
        return "POST 用户名字是" + user.getUsername();
    }

}
