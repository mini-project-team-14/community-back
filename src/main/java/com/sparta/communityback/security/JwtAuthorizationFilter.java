package com.sparta.communityback.security;

import com.sparta.communityback.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
//         String tokenValue = jwtUtil.getTokenFromRequest(req);
        String tokenValue = jwtUtil.getJwtFromHeader(req);
//        if (StringUtils.hasText(tokenValue)) {
            if (!jwtUtil.validateToken(tokenValue, req, res)) {
                log.error("Token Error: 토큰 재발급이 되었다면 에러가 아닙니다.");
                tokenValue = res.getHeader(jwtUtil.ACCESS_TOKEN).substring(7);

                System.out.println("tokenValue = " + tokenValue);
            }
            Claims info = jwtUtil.getUserInfoFromToken(tokenValue);

            try {
                setAuthentication(info.getSubject());// 만일 토큰에 넣어주는 방식을 바꾸게 될경우 여기를 수정
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new AuthorizationServiceException(e.getMessage());

            }
//        }

        filterChain.doFilter(req, res);
    }
    // 인증 처리
    public void setAuthentication(String username) {
        Authentication authentication = createAuthentication(username);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("인증처리: {}", (SecurityContextHolder.getContext().getAuthentication()));
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}