package com.multi.laptellect.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/redis-test")
@RestController
public class RedisTest {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping()
    public String redisTest() {
        redisTemplate.opsForValue().set("testKey", "테스트 성공");
        return redisTemplate.opsForValue().get("testKey");
    }
}