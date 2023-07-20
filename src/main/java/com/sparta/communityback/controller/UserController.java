package com.sparta.communityback.controller;

import com.sparta.communityback.dto.SignupRequestDto;
import com.sparta.communityback.dto.StatusResponseDto;
import com.sparta.communityback.dto.UsernameRequestDto;
import com.sparta.communityback.jwt.JwtUtil;
import com.sparta.communityback.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

    @PostMapping("/logout")
    public ResponseEntity<StatusResponseDto> logout(HttpServletRequest request) {
        return ResponseEntity.ok()
                .body(userService.logout(request));
    }

}