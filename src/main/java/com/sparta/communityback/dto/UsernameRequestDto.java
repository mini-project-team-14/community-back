package com.sparta.communityback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UsernameRequestDto {
    @NotBlank(message = "username은 공백일 수 없습니다.")
    @Size(min = 2, max = 15, message = "username_length")
    @Pattern(regexp = "^[a-z0-9]+$", message = "username_rule")
    private String username;
}
