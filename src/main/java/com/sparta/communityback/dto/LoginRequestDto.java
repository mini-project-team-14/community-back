package com.sparta.communityback.dto;

import lombok.Getter;

@Getter
public class LoginRequestDto {
    private String nickname;
    private String password;
}