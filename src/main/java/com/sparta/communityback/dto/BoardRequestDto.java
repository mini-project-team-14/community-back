package com.sparta.communityback.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class BoardRequestDto {
    @NotBlank
    private String category;
}
