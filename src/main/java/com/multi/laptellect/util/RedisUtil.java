package com.multi.laptellect.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Redis Util 클래스
 *
 * @author : 이강석
 * @fileName : RedisUtil.java
 * @since : 2024-07-26
 */
@Component
@RequiredArgsConstructor
public class RedisUtil { // Redis 사용 클래스
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Hash 형태로 Redis에 저장하는 메서드
     *
     * @param key   키값
     * @param value 밸류값
     */
    public void setData(String key, String value) { // Key Value 구조로 저장
        ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value);
    }

    /**
     * Hash 형태로 Redis에 저장하는 메서드, TTL로 유효시간 설정 가능
     *
     * @param key      키값
     * @param value    밸류값
     * @param duration 유효시간
     */
    public void setDataExpire(String key, String value, long duration) { // 유효 시간 설정
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    /**
     * 키값을 통해 Redis에서 Value를 가져오는 메서드
     *
     * @param key 키값
     * @return 밸류값 리턴
     */
    public String getData(String key) { // 데이터 가져오기
        ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    /**
     * Redis에서 키값을 사용해 데이터를 삭제하는 메서드
     *
     * @param key 키값
     */
    public void deleteData(String key) { // 데이터 삭제
        redisTemplate.delete(key);
    }
}
