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
    private String accessToken;

    @Column(unique = true)
    private String refreshToken;

//    @Column(unique = true)
//    private String accessToken;

    public RefreshToken(String refreshToken, String accessToken) {
//        this.userid = userId;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }
}