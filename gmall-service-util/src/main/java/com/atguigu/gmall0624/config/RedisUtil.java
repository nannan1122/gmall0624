package com.atguigu.gmall0624.config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {
    // 创建JedisPool，初始化
    private JedisPool jedisPool;
    //// 初始化连接池
    public void initJedisPool(String host,int port,int timeout){
        JedisPoolConfig jedisPoolConfig=new JedisPoolConfig();
        // 设置连接池最大核心数
        jedisPoolConfig.setMaxTotal(200);
        // 设置等待时间
        jedisPoolConfig.setMaxWaitMillis(10*1000);
        // 最少剩余数
        jedisPoolConfig.setMinIdle(10);
        // 排队等待
        jedisPoolConfig.setBlockWhenExhausted(true);
        // 设置当用户获取到jedis 时，做自检
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPool=new JedisPool(jedisPoolConfig,host,port,timeout);

    }


    //获取Jedis
    public Jedis getJedis(){
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }

}
