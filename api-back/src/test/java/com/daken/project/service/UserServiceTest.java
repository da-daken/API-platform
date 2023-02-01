package com.daken.project.service;


import com.alibaba.fastjson.JSON;
import com.daken.apiclientsdk.client.ApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 用户服务测试
 *
 * @author daken
 */
@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void testAddUser() {
        String str = "{\"0\":\"zhangsan\",\"1\":\"lisi\",\"2\":\"wangwu\",\"3\":\"maliu\"}";
        Map maps = (Map) JSON.parse(str);
        for (Object map : maps.entrySet()) {
            System.out.println(((Map.Entry) map).getKey() + " " + ((Map.Entry) map).getValue());
        }
    }
    @Test
    void testClass() throws NoSuchMethodException, ClassNotFoundException {
        ApiClient tempClient = new ApiClient("api", "abcdefgh");
        Method method = tempClient.getClass().getMethod("getUsernameByPost", Class.forName("com.daken.apiclientsdk.model.User"));
        Method[] methods = tempClient.getClass().getMethods();
        Class<?>[] parameterTypes = method.getParameterTypes();
//        methods[0].invoke(tempClient, );

        System.out.println(methods[0].toString());
    }

 }
