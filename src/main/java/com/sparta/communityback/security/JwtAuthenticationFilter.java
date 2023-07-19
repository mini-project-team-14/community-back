package com.sparta.communityback.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.communityback.dto.LoginRequestDto;
import com.sparta.communityback.dto.ResultResponseDto;
import com.sparta.communityback.entity.RefreshToken;
import com.sparta.communityback.entity.UserRoleEnum;
import com.sparta.communityback.jwt.JwtUtil;
import com.sparta.communityback.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        log.info("request uri: {}", request.getRequestURI());


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
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();
        Long userId = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getId();

        String accessToken = jwtUtil.createAccessToken(username, role);
        response.addHeader(JwtUtil.ACCESS_TOKEN, accessToken);

        if(redisService.getRefreshToken(userId) == null) {
            // redis userid값으로 조회후 동일한 값이 존재한다면 중복로그인은 불가능하다로 에러처리
            String refreshToken = jwtUtil.createRefreshToken(username);
            response.addHeader(JwtUtil.REFRESH_TOKEN, refreshToken);
            // redis에 저장
            redisService.setRefreshToken(new RefreshToken(refreshToken, userId));
        }

            response.setStatus(200);
            new ObjectMapper().writeValue(response.getOutputStream(), new ResultResponseDto("로그인 성공"));
        }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(400);
        new ObjectMapper().writeValue(response.getOutputStream(), new ResultResponseDto("아이디와 비밀번호를 한번 더 확인해 주세요"));
    }
}