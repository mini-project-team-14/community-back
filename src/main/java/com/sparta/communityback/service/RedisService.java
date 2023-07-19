package com.sparta.communityback.service;

import com.sparta.communityback.entity.RefreshToken;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate redisTemplate;

    @Transactional
    public void setRefreshToken(RefreshToken refreshToken){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(refreshToken.getRefreshToken(), String.valueOf(refreshToken.getUserid()));
        redisTemplate.expire(String.valueOf(refreshToken.getUserid()), (60 * 60 * 24 * 14 + 60), TimeUnit.SECONDS);
    }

    public String getRefreshToken(String refreshToken){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(refreshToken);
    }
    public String getRefreshToken(Long userId) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();

        // Redis에서 key를 찾기 위해 모든 키를 순회합니다.
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            String value = values.get(key);
            if (value != null && Long.parseLong(value) == userId) {
                return key; // userId와 일치하는 value를 가진 key를 반환합니다.
            }
        }

        // 일치하는 userId가 없을 경우 null을 반환합니다.
        return null;
    }
}