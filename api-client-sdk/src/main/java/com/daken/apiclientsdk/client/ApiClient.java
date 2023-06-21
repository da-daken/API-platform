package com.daken.apiclientsdk.client;


import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.daken.apiclientsdk.model.AvatarParams;
import com.daken.apiclientsdk.model.BaiduHotParams;
import com.daken.apiclientsdk.model.User;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.daken.apiclientsdk.utils.SignUtils.genSign;

/**
 * 第三方调用者 sdk
 */
@Slf4j
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

    /**
     * 获取随机头像接口
     * @param avatarParams
     * @return
     */
    public String getAvatarUrl(AvatarParams avatarParams){
        String params = JSON.toJSONString(avatarParams);
        String avatarUrl = postRequest(params, "/api/avatar/avatarUrl");
        return avatarUrl;
    }

    /**
     * 获取百度热搜接口
     * @param baiduHotParams
     * @return
     */
    public String getBaiduHotInfo(BaiduHotParams baiduHotParams){
        String params = JSON.toJSONString(baiduHotParams);
        String baiduHotInfo = postRequest(params, "/api/baidu/baiduHotInfo");
        return baiduHotInfo;
    }



    /**
     * 获取用户名称接口(测试)
     * @param user
     * @return
     * @throws UnsupportedEncodingException
     */
    public String getUsernameByPost(User user) throws UnsupportedEncodingException {
        String json = JSONUtil.toJsonStr(user);
        String result = postRequest(json, "/api/name/user");
        return result;
    }

    /**
     * 封装post请求
     * @param parameters
     * @param url
     * @return
     */
    private String postRequest(String parameters,String url){
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + url)
                .addHeaders(getHeaderMap(parameters))
                .body(parameters)
                .execute();
        log.info("返回状态：{}  返回结果 : {}", httpResponse.getStatus(), httpResponse.body());
        String result = httpResponse.body();
        return result;
    }


    /**
     * 规定好请求头，在具体接口里面用hutool发送http请求
     * @param body
     * @return
     * @throws UnsupportedEncodingException
     */
    private Map<String, String> getHeaderMap(String body){
        String encode = null;
        try {
            encode = URLEncoder.encode(body, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("body", encode);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        // 将secretKey用md5加密后放入请求头种，一定不能直接发送
        hashMap.put("sign", genSign(encode, secretKey));
        return hashMap;
    }
}
