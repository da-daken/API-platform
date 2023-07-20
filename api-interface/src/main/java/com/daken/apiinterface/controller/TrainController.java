package com.daken.apiinterface.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;


@RestController
@RequestMapping("/train")
public class TrainController {
    /**
     * 获取火车的信息
     * @return
     */
    @GetMapping("/getTrainInfo")
    public String getTrainInfo(String train_no, String from_station_telecode, String to_station_telecode, String depart_date) throws URISyntaxException {
        // https://kyfw.12306.cn/otn/czxx/queryByTrainNo
        URI uri = new URIBuilder().setScheme("https")
                .setHost("kyfw.12306.cn")
                .setPath("/otn/czxx/queryByTrainNo")
                .setParameter("train_no", train_no)
                .setParameter("from_station_telecode", from_station_telecode)
                .setParameter("to_station_telecode", to_station_telecode)
                .setParameter("depart_date", depart_date)
                .build();
        String result = HttpUtil.get(uri.toString());
        return result;
    }
}
