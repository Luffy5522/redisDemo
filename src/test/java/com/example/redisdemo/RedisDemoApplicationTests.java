package com.example.redisdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

@SpringBootTest
class RedisDemoApplicationTests {

    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        // 写入数据
            redisTemplate.opsForValue().set("name","张三");
        // 获取数据
        Object name = redisTemplate.opsForValue().get("name");
        System.out.println("name:" + name);

    }

}
