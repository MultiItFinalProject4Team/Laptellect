package com.multi.laptellect.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisUtil { // Redis 사용 클래스
    private final RedisTemplate<String, String> redisTemplate;

    public void setData(String key, String value) { // Key Value 구조로 저장
        ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value);
    }

    public void setDataExpire(String key, String value, long duration) { // 유효 시간 설정
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    public String getData(String key) { // 데이터 가져오기
        ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public void deleteData(String key) { // 데이터 삭제
        redisTemplate.delete(key);
    }
}
