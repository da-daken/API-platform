package com.daken.project.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.daken.apiclientsdk.utils.SignUtils.genSign;
@SpringBootTest
public class GetTest {
    private static final String GATEWAY_HOST = "http://localhost:8090";
    @Test
    public void getNameByGet() throws UnsupportedEncodingException {
        HashMap<String, Object> param = new HashMap<>();
        param.put("name", "daken");
        HttpResponse httpResponse = HttpRequest.get(GATEWAY_HOST+"/api/name/get")
                .addHeaders(getHeaderMap("123"))
                .form(param)
                .execute();
        System.out.println(httpResponse.body());
    }
    private Map<String, String> getHeaderMap(String body){
        String encode = null;
        try {
            encode = URLEncoder.encode(body, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", "[B@762f92e9");
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("body", encode);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        // 将secretKey用md5加密后放入请求头种，一定不能直接发送
        hashMap.put("sign", genSign(encode, "[B@747ad540"));
        return hashMap;
    }
}
