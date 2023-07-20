package com.sparta.communityback.service;

import com.sparta.communityback.dto.SignupRequestDto;
import com.sparta.communityback.dto.StatusResponseDto;
import com.sparta.communityback.dto.UsernameRequestDto;
import com.sparta.communityback.entity.User;
import com.sparta.communityback.entity.UserRoleEnum;
import com.sparta.communityback.jwt.JwtUtil;
import com.sparta.communityback.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate redisTemplate;
    private final JwtUtil jwtUtil;

    @Value("${adimin.token}") // Base64 Encode 한 SecretKey
    private String ADMIN_TOKEN;


    @Transactional
    public StatusResponseDto signup(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String nickname = requestDto.getNickname();
        // 회원 중복 확인
        checkUsername(username);
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
        return new StatusResponseDto(HttpStatus.OK.value(), "회원가입 성공!");
    }

    public StatusResponseDto logout(HttpServletRequest request) {
        String refreshToken = request.getHeader(jwtUtil.REFRESH_TOKEN);
        redisTemplate.delete(refreshToken);
        return new StatusResponseDto(HttpStatus.OK.value(), "로그아웃 성공!");
    }

    public StatusResponseDto checkUsername(UsernameRequestDto requestDto) {
        String username = requestDto.getUsername();
        checkUsername(username);
        return new StatusResponseDto(HttpStatus.OK.value(), "사용가능한 Username입니다.");
    }

    private void checkUsername(String username) {
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }
    }
}
