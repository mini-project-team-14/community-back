package com.sparta.communityback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupRequestDto {

    @NotBlank(message = "username은 공백일 수 없습니다.")
    @Size(min = 2, max = 15, message = "두글자 이상, 15글자 이하만 가능합니다.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "소문자 영어와 숫자만 사용 가능합니다.")
    private String username;

    @NotBlank(message = "password는 공백일 수 없습니다.")
    @Size(min = 4, message = "최소 길이는 4입니다.")
    private String password;

    @NotBlank(message = "nickname은 공백일 수 없습니다.")
    @Size(min = 2, max = 15, message = "두글자 이상, 15글자 이하만 가능합니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z]+$", message = "한글 또는 영어만 입력 가능합니다.")
    private String nickname;

    private boolean admin = false;

    private String adminToken = "";

}
