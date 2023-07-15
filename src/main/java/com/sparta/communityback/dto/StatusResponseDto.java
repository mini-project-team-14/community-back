package com.sparta.communityback.dto;

import lombok.Getter;

@Getter
public class StatusResponseDto {
    private String message;
    private int statusCode;

    public StatusResponseDto(int statusCode, String message) {
        this.message = message;
        this.statusCode = statusCode;
    }
}