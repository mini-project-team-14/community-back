package com.sparta.communityback.security;

import com.sparta.communityback.entity.RefreshToken;
import com.sparta.communityback.jwt.JwtUtil;
import com.sparta.communityback.service.RedisService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
//         String accessTokenFromHeader = jwtUtil.getTokenFromRequest(req);
        // access 토큰 확인
        String accessTokenValueFromHeader = jwtUtil.getAccessTokenFromHeader(req);
//        if (StringUtils.hasText(accessTokenFromHeader)) {
        String accessTokenValue = "";
        if (jwtUtil.validateAccessToken(accessTokenValueFromHeader)) {
            log.info("가지고 있던 accessToken이 유효함");
            accessTokenValue = accessTokenValueFromHeader;
        } else {
            log.info("가지고 있던 accessToken이 유효하지 않음");
            accessTokenValue = jwtUtil.validateTokens(req, res).substring(7);
            System.out.println("accessTokenValue = " + accessTokenValue);
            log.info("");
            String newRefreshToken = res.getHeader(jwtUtil.REFRESH_TOKEN);
            redisService.setRefreshToken(new RefreshToken(newRefreshToken, accessTokenValue));
        }
//            if (!jwtUtil.validateTokens(accessTokenFromHeader, req, res)) {
//                // access 토큰 만료 또는 없을시 수행
//                log.error("Token Error: 토큰 재발급이 되었다면 에러가 아닙니다.");
//                accessTokenFromHeader = res.getHeader(jwtUtil.ACCESS_TOKEN).substring(7);
//
//                System.out.println("accessTokenFromHeader = " + accessTokenFromHeader);
//            }
            Claims info = jwtUtil.getUserInfoFromToken(accessTokenValue);

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