package com.sparta.communityback.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class SignupRequestDto {
    @NotBlank(message = "username은 공백일 수 없습니다.")
    private String username;
    @NotBlank(message = "password는 공백일 수 없습니다.")
    private String password;
    @NotBlank(message = "nickname은 공백일 수 없습니다.")
    private String nickname;
    private boolean admin = false;
    private String adminToken = "";

}
