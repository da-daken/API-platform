package com.daken.project.utils;

import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import org.springframework.stereotype.Component;


import java.util.Random;

/**
 * 生成ak/sk的工具类
 */
@Component
public class GenerateAkSkUtils {

    private static final Integer RANDOM_SK_LEN = 8;
    private static final Integer RANDOM_AK_LEN = 5;
    private static final Integer BOUND = 62;
    private static final String STR = "abcdefghijklmnopqrstuvwxyzABCDEfGHIJKLMNOPQRSTUVWXYZ0123456789";


    /**
     * 生成 ak
     * @param userAccount
     * @return
     */
    public String generateAk(String userAccount){
        HMac mac = new HMac(HmacAlgorithm.HmacSHA256, getRandomSecret(userAccount).getBytes());
        byte[] digest = mac.digest(getRandomString(RANDOM_AK_LEN));
        return digest.toString();
    }

    /**
     * 生成 sk
     * @param userAccount
     * @return
     */
    public String generateSk(String userAccount){
        HMac mac = new HMac(HmacAlgorithm.HmacSHA256, getRandomSecret(userAccount).getBytes());
        byte[] digest = mac.digest(getRandomString(RANDOM_SK_LEN));
        return digest.toString();
    }

    /**
     * 时间戳，userId作为密钥（保证每个用户都不相同）
     * @param userAccount
     * @return
     */
    private String getRandomSecret(String userAccount) {
        String currentTimeMillis = String.valueOf(System.currentTimeMillis());
        return userAccount + currentTimeMillis;
    }

    private String getRandomString(Integer len){
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<len;i++){
            int number = random.nextInt(BOUND);
            sb.append(STR.charAt(number));
        }
        return sb.toString();
    }
}
