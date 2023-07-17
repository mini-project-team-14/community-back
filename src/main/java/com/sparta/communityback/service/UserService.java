package com.sparta.communityback.service;

import com.sparta.communityback.dto.LoginRequestDto;
import com.sparta.communityback.dto.ResultResponseDto;
import com.sparta.communityback.dto.SignupRequestDto;
import com.sparta.communityback.entity.User;
import com.sparta.communityback.entity.UserRoleEnum;
import com.sparta.communityback.jwt.JwtUtil;
import com.sparta.communityback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${adimin.token}") // Base64 Encode 한 SecretKey
    private String ADMIN_TOKEN;


    @Transactional
    public ResponseEntity<ResultResponseDto> signup(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String nickname = requestDto.getNickname();
        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }
        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }


        User user = new User(username, password, nickname, role);
        userRepository.save(user);
        return new ResponseEntity<>(new ResultResponseDto("회원가입 성공!"), HttpStatus.OK);
    }
}
