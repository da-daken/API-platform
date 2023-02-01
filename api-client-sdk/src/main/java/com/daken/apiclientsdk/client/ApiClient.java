package com.daken.apiclientsdk.client;


import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.daken.apiclientsdk.model.User;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.daken.apiclientsdk.utils.SignUtils.genSign;

public class ApiClient {
    private String accessKey;

    private String secretKey;

    private static final String GATEWAY_HOST = "http://localhost:8090";

    public ApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name) throws UnsupportedEncodingException {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        HttpResponse httpResponse = HttpRequest.get(GATEWAY_HOST+"/api/name/")
                .addHeaders(getHeaderMap(name))
                .body(name)
                .execute();
        return httpResponse.body();
    }

    public String getNameByPost(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.post(GATEWAY_HOST+"/api/name/", paramMap);
        return result;
    }

    // 规定好请求头，在具体接口里面用hutool发送http请求
    private Map<String, String> getHeaderMap(String body) throws UnsupportedEncodingException {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("body", URLEncoder.encode(body, "utf-8"));
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        // 将secretKey用md5加密后放入请求头种，一定不能直接发送
        hashMap.put("sign", genSign(body, secretKey));
        return hashMap;
    }

    public String getUsernameByPost(User user) throws UnsupportedEncodingException {
           String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST+"/api/name/user")
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute();
        System.out.println(httpResponse.getStatus());
        String result = httpResponse.body();
        System.out.println(result);
        return result;
    }

}
