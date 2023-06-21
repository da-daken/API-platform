package com.daken.apiinterface.controller;

import com.daken.apiclientsdk.model.AvatarParams;
import com.daken.apiinterface.common.BaseResponse;
import com.daken.apiinterface.common.ErrorCode;
import com.daken.apiinterface.common.ResultUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@RequestMapping("/avatar")
public class AvatarController {

    /**
     * 获取随机头像的 url
     * @return
     */
    @PostMapping("/avatarUrl")
    public BaseResponse<String> getAvatarUrl(@RequestBody(required = false) AvatarParams avatarParams, HttpServletRequest request){
        //https://restapi.amap.com/v3/weather/weatherInfo?
        String avatarUrl = "https://www.loliapi.com/acg/pp/";
        String redirectUrl ;
        try {
            redirectUrl = getRedirectUrl(avatarUrl);
        } catch (Exception e){
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "请求接口异常，请联系管理员");
        }
        return ResultUtils.success(redirectUrl);
    }

    /**
     * 获取重定向地址
     * @param path
     * @return
     * @throws Exception
     */
    private String getRedirectUrl(String path) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(path)
                .openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.setConnectTimeout(5000);
        String location = conn.getHeaderField("Location");
        return location;
    }
}
