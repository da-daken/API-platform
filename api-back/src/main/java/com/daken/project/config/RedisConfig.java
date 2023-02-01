package com.daken.project.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {


    /**
     * 配置RedissonClient
     * @return RedissonClient 提供大量的工具方法
     */
    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        // 添加集群的 redis
//        config.useClusterServers()
//                .addNodeAddress("redis://127.0.0.1:7181", "redis://localhost:6379");
        //此处调用的方法为单节点的 redis
        config.useSingleServer().setAddress("redis://localhost:6379").setDatabase(3);
        return Redisson.create(config);
    }
}
