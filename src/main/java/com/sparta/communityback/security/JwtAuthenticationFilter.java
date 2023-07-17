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
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;

//@CrossOrigin(originPatterns = "http://localhost:3000")
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
            throw new AuthenticationException(e.getMessage()) {
            };
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
//            response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
//            response.setHeader("Access-Control-Allow-Origin", "http://13.125.15.196:8080/");
//            response.setHeader("Access-Control-Allow-Methods", "POST");
//            response.setHeader("Access-Control-Allow-Methods", "GET");
//            response.setHeader("Access-Control-Allow-Methods", "OPTIONS");
//            response.setHeader("Access-Control-Allow-Methods", "PUT");
//            response.setHeader("Access-Control-Allow-Methods", "DELETE");
//            response.setHeader("Access-Control-Max-Age", "3600");
//            response.setHeader("Access-Control-Allow-Headers", "x-requested-with, origin, content-type, accept");

            response.setStatus(200);
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
//        new ObjectMapper().writeValue(response.getOutputStream(), "로그인 성공");

            new ObjectMapper().writeValue(response.getOutputStream(), new ResultResponseDto("로그인 성공"));
//            String json = new ObjectMapper().writeValueAsString(new ResultResponseDto("로그인 성공"));
//            response.getWriter().write(json);
        } else {
            response.setStatus(200);
            new ObjectMapper().writeValue(response.getOutputStream(), new ResultResponseDto("로그인 상태입니다."));
//            String json = new ObjectMapper().writeValueAsString(new ResultResponseDto("로그인 상태입니다."));
//            response.getWriter().write(json);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(400);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
//        String json = new ObjectMapper().writeValueAsString(new ResultResponseDto("로그인 실패"));
//        response.getWriter().write(json);
        new ObjectMapper().writeValue(response.getOutputStream(), new ResultResponseDto("로그인 실패"));
    }

}