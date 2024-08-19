package com.multi.laptellect.recommend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheService {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisCacheService(RedisTemplate<String, String> redisTemplate) { //RedisTemplate주입 할당
        this.redisTemplate = redisTemplate;
    }

    public void setCurationResult(String key, String value) {//key 받아서 레디스에 저장
        redisTemplate.opsForValue().set(key, value, 30, TimeUnit.MINUTES);
    }

    public String getCurationResult(String key) { //key 받아서 레디스에서 value 가져오기
        return redisTemplate.opsForValue().get(key);
    }
}