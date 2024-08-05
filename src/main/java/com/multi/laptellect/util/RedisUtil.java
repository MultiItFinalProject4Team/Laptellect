package com.multi.laptellect.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

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

    /**
     * List 형태로 Redis에 저장하는 메서드
     *
     * @param key    키값
     * @param values 밸류값
     */
    public void setListData(String key, List<String> values) { // 리스트 데이터 저장
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        listOperations.rightPushAll(key, values);
    }

    /**
     * List 형태로 Redis에 저장하는 메서드 + TTL 설정
     *
     * @param key      키값
     * @param values   밸류값
     * @param duration 유효시간(시간단위)
     */
    public void setListDataExpire(String key, List<String> values, long duration) { // 리스트 데이터 저장 및 TTL 설정
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        listOperations.rightPushAll(key, values);
        redisTemplate.expire(key, Duration.ofSeconds(duration));
    }

    /**
     * 키값을 통해 Redis에서 List를 가져오는 메서드
     *
     * @param key 키값
     * @return 리스트 리턴
     */
    public List<String> getListData(String key) { // 리스트 데이터 가져오기
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        return listOperations.range(key, 0, -1);
    }

    /**
     * 리스트에 항목 추가하는 메서드
     *
     * @param key   키값
     * @param value 밸류값
     */
    public void addToList(String key, String value) { // 리스트에 항목 추가
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        listOperations.rightPush(key, value);
    }

    /**
     * 리스트에서 항목을 삭제 하는 메서드
     *
     * @param key   키값
     * @param count 횟수( [a, b, c, a]일 때 0이면 모든 항목, 1이면 a 하나만, 2면 a 둘 다 삭제
     * @param value 벨류 값
     */
    public void deleteToList(String key, long count, String value) { // 리스트에서 항목 삭제
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        listOperations.remove(key, count, value);
    }
}
