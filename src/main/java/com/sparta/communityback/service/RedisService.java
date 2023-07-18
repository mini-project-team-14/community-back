package com.sparta.communityback.service;

import com.sparta.communityback.entity.RefreshToken;
import com.sparta.communityback.repository.RedisRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate redisTemplate;
    private final RedisRepository redisRepository;

    @Transactional
    public void setRefreshToken(RefreshToken refreshToken){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(refreshToken.getUserid().toString(),refreshToken.getRefreshToken());
    }

//    @Transactional
//    public void setRefreshToken(RefreshToken refreshToken){
//        redisRepository.save(refreshToken);
//    }

    public String getRefreshToken(Long userId) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        redisRepository.findById(userId);
        return values.get(userId);
    }
}