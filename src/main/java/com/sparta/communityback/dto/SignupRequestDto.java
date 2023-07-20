package com.sparta.communityback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class SignupRequestDto {

    @NotBlank(message = "username은 공백일 수 없습니다.")
    @Size(min = 2, max = 15, message = "username_length")
    @Pattern(regexp = "^[a-z0-9]+$", message = "username_rule")
    private String username;

    @NotBlank(message = "password는 공백일 수 없습니다.")
    @Size(min = 4, message = "password_length")
    private String password;

    @NotBlank(message = "nickname은 공백일 수 없습니다.")
    @Size(min = 2, max = 15, message = "nickname_length")
    @Pattern(regexp = "^[가-힣a-zA-Z]+$", message = "nickname_rule")
    private String nickname;

    private boolean admin = false;

    private String adminToken = "";

}
