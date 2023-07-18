package com.sparta.communityback.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Entity
@Getter
@NoArgsConstructor
@RedisHash(timeToLive = 60 * 60 * 24 * 14 + 60) // 14일 + 60초
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userid;

    private String refreshToken;

//    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private User user;

    public RefreshToken(String refreshToken, Long userId) {
        this.userid = userId;
        this.refreshToken = refreshToken;
    }
}