package com.atguigu.gmall0624.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host:disabled}")
    private String host;

    @Value("${spring.redis.port:0}")
    private int port;

    @Value("${spring.redis.timeout:10000}")
    private int timeout;


    @Bean //注入spring容器
    public RedisUtil getRedisUtil(){
        // 表示配置文件中没有host
        if("disabled".equals(host)){
            return null;
        }
        RedisUtil redisUtil =new RedisUtil();
        redisUtil.initJedisPool(host,port,timeout);

        return redisUtil;
    }
}
