package com.multi.laptellect.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public void setListData(String key, String values) { // 리스트 데이터 저장
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

    public void setHashData(String key, String field, String value) { // Hash 데이터 저장
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(key, field, value);
    }

    /**
     * Hash 데이터 Set
     *
     * @param key      키값
     * @param field    필드값
     * @param value    벨류값
     * @param duration TTL 유효기간
     */
    public void setHashDataExpire(String key, String field, String value, Long duration) { // Hash 데이터 저장
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(key, field, value);
        redisTemplate.expire(key, Duration.ofSeconds(duration));
    }

    /**
     * 특정 필드 값 업데이트
     *
     * @param key      키값
     * @param field    필드값
     * @param value    업데이트 할 벨류값
     * @param duration TTL 유효기간
     */
    public void updateHashDataExpire(String key, String field, String value, Long duration) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(key, field, value);
        redisTemplate.expire(key, Duration.ofSeconds(duration));
    }

    /**
     * 특정 Value 값 가져오가
     *
     * @param key   키값
     * @param field 필드값
     * @return 특정 필드의 Value 값 반환
     */
    public String getHashData(String key, String field) { // Hash 데이터 가져오기
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        return hashOperations.get(key, field);
    }

    /**
     * 전체 Field Value 값 가져오가
     *
     * @param key 키값
     * @return HashMap 형태로 Field, Value 값 반환
     */
    public Map<String, String> getAllHashData(String key) { // 전체 Hasg 데이터 가져오기
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        return hashOperations.entries(key);
    }

    /**
     * 특정 필드 삭제
     *
     * @param key   키값
     * @param field 필드값
     */
    public void deleteHashData(String key, String field) { // Hash 데이터 삭제
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.delete(key, field);
    }

    /**
     * sorted set 데이터 추가
     *
     * @param key   키값
     * @param value 밸류 값
     * @param score 스코어
     */
    public void addSortedSetData(String key, String value, double score) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add(key, value, score);
    }

    /**
     * 범위 내의 sorted set 가져오기
     *
     * @param key   키값
     * @param start 시작 값
     * @param end   끝나는 값
     * @return the sorted set range
     */
    public Set<String> getSortedSetRange(String key, long start, long end) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.range(key, start, end);
    }

    /**
     * 특정 항목 가져오기
     *
     * @param key   키값
     * @param value 밸류값
     * @return the sorted set score
     */
    public Double getSortedSetScore(String key, String value) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.score(key, value);
    }

    /**
     * 특정 항목 삭제
     *
     * @param key   키값
     * @param value 밸류값
     */
    public void removeSortedSetData(String key, String value) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.remove(key, value);
    }

    /**
     * 스프링 세션 수 카운트
     *
     * @return 현재 접속중인 사용자 수 반환
     */
    public int getActiveUserCount() {
        return redisTemplate.keys("spring:session:sessions:*").size();
    }
}
