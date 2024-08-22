package com.multi.laptellect.recommend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multi.laptellect.recommend.laptop.model.dto.CurationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;


    @Autowired
    public RedisCacheService(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;

    }

    public String saveCurationResult(CurationDTO curationDTO) throws JsonProcessingException {
        String cacheKey = UUID.randomUUID().toString();
        String curationJson = objectMapper.writeValueAsString(curationDTO);
        redisTemplate.opsForValue().set(cacheKey, curationJson, 30, TimeUnit.MINUTES);
        return cacheKey;
    }

    public CurationDTO getCurationResult(String key) throws JsonProcessingException {
        String curationJson = redisTemplate.opsForValue().get(key);
        return objectMapper.readValue(curationJson, CurationDTO.class);
    }


}