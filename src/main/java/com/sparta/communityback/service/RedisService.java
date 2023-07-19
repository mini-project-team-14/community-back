package com.sparta.communityback.service;

import com.sparta.communityback.entity.RefreshToken;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

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
}