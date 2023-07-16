package com.sparta.communityback.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.communityback.dto.LoginRequestDto;
import com.sparta.communityback.dto.ResultResponseDto;
import com.sparta.communityback.entity.UserRoleEnum;
import com.sparta.communityback.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/user/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
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

        String headerToken = request.getHeader(JwtUtil.AUTHORIZATION_HEADER); // 헤더에서 토큰을 가져옴

        if (headerToken == null) {
            String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
            UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();

            String token = jwtUtil.createToken(username, role);
            response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

            response.setStatus(200);
//        new ObjectMapper().writeValue(response.getOutputStream(), "로그인 성공");
            new ObjectMapper().writeValue(response.getOutputStream(), new ResultResponseDto("로그인 성공"));
        } else {
            response.setStatus(200);
            new ObjectMapper().writeValue(response.getOutputStream(), new ResultResponseDto("로그인 상태입니다."));
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(400);
        new ObjectMapper().writeValue(response.getOutputStream(), new ResultResponseDto("로그인 실패"));
    }

}