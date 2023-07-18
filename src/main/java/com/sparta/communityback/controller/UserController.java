package com.sparta.communityback.controller;

import com.sparta.communityback.dto.ResultResponseDto;
import com.sparta.communityback.dto.SignupRequestDto;
import com.sparta.communityback.dto.StatusResponseDto;
import com.sparta.communityback.dto.UsernameRequestDto;
import com.sparta.communityback.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<StatusResponseDto> signup(@RequestBody @Valid SignupRequestDto requestDto) {
//    , BindingResult bindingResult) {
//        // Validation 예외처리
//        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
//        if(fieldErrors.size() > 0) {
//            for (FieldError fieldError : bindingResult.getFieldErrors()) {
//                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
//            }
////            return "redirect:/api/user/signup";
//        }
        return ResponseEntity.ok()
                .body(userService.signup(requestDto));
    }

    @PostMapping("/signup/check")
    public ResponseEntity<StatusResponseDto> checkUsername(@RequestBody @Valid UsernameRequestDto requestDto) {
        return ResponseEntity.ok()
                .body(userService.checkUsername(requestDto));
    }
}