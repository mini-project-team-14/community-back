package com.sparta.communityback.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.communityback.dto.LoginRequestDto;
import com.sparta.communityback.entity.RefreshToken;
import com.sparta.communityback.dto.StatusResponseDto;
import com.sparta.communityback.entity.User;
import com.sparta.communityback.entity.UserRoleEnum;
import com.sparta.communityback.jwt.JwtUtil;
import com.sparta.communityback.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("attemptAuthentication");
        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    requestDto.getUsername(),
                    requestDto.getPassword()
            );
            return getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        log.info("successfulAuthentication");
        User user = ((UserDetailsImpl) authResult.getPrincipal()).getUser();
        String username = user.getUsername();
        String nickname = user.getNickname();
        UserRoleEnum role = user.getRole();
        Long userId = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getUserId();

        String accessToken = jwtUtil.createAccessToken(username, nickname, role);
        response.addHeader(JwtUtil.ACCESS_TOKEN, accessToken);

        // 중복 로그인 가능한 계정 수를 제한시키기
        if (redisService.limitAccess(username)) {
            log.info("접속수 제한 초과");
            redisService.deleteOldRefreshToken(username);
        }
        String newRefreshToken = jwtUtil.createRefreshToken(username);
        response.addHeader(JwtUtil.REFRESH_TOKEN, newRefreshToken);
        // redis에 저장
        redisService.setRefreshToken(new RefreshToken(newRefreshToken, accessToken));



        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        new ObjectMapper().writeValue(response.getOutputStream(),
                new StatusResponseDto(HttpStatus.OK.value(), "로그인 성공")
        );

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        log.info("unsuccessfulAuthentication");
        response.setStatus(400);
        response.setContentType("application/json;charset=UTF-8");
        new ObjectMapper().writeValue(response.getOutputStream(),
                new StatusResponseDto(HttpStatus.BAD_REQUEST.value(), "로그인 실패")
        );
    }

}