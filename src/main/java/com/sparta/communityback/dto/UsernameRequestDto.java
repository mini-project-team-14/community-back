package com.sparta.communityback.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UsernameRequestDto {
    @NotBlank(message = "username은 공백일 수 없습니다.")
    private String username;
}
