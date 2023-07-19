package com.sparta.communityback.entity;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor
@RedisHash(timeToLive = 60 * 60 * 24 * 14 + 60) // 14일 + 60초
public class RefreshToken {

    @Id
    private Long userid;

    @Column(unique = true)
    private String refreshToken;

    public RefreshToken(String refreshToken, Long userId) {
        this.userid = userId;
        this.refreshToken = refreshToken;
    }
}