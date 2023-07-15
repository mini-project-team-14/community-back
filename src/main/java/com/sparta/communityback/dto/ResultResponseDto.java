package com.sparta.communityback.dto;

import lombok.Data;

@Data
public class ResultResponseDto {
    private String msg;

    public ResultResponseDto(String msg) {
        this.msg = msg;
    }
}
