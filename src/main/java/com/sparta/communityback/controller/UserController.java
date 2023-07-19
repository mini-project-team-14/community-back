package com.sparta.communityback.controller;

import com.sparta.communityback.dto.SignupRequestDto;
import com.sparta.communityback.dto.StatusResponseDto;
import com.sparta.communityback.dto.UsernameRequestDto;
import com.sparta.communityback.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<StatusResponseDto> signup(@RequestBody @Valid SignupRequestDto requestDto) {
        return ResponseEntity.ok()
                .body(userService.signup(requestDto));
    }

    @PostMapping("/signup/check")
    public ResponseEntity<StatusResponseDto> checkUsername(@RequestBody @Valid UsernameRequestDto requestDto) {
        return ResponseEntity.ok()
                .body(userService.checkUsername(requestDto));
    }
}