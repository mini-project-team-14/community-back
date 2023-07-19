package com.sparta.communityback.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PostRequestDto {
    @NotEmpty(message = "title의 길이는 1에서 255 사이여야 합니다")
    @Size(min = 1, max = 255, message = "title의 길이는 1에서 255 사이여야 합니다")
    private String title;
    @NotEmpty(message = "content의 길이는 1에서 10000 사이여야 합니다")
    @Size(min = 1, max = 10000, message = "content의 길이는 1에서 10000 사이여야 합니다")
    private String content;
}
