package com.sparta.communityback.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class BoardRequestDto {
    @NotEmpty(message = "category의 길이는 1에서 255 사이여야 합니다")
    @Size(min = 1, max = 255, message = "category의 길이는 1에서 255 사이여야 합니다")
    private String category;
}
