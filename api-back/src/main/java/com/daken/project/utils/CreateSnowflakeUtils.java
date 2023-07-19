package com.daken.project.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 维护雪花算法为单例对象
 *
 * @author daken
 */
@Component
public class CreateSnowflakeUtils {

    @Bean
    public Snowflake snowflake(){
        return IdUtil.createSnowflake(1,1);
    }
}
