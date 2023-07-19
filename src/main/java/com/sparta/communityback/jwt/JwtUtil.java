package com.sparta.communityback.jwt;

import com.sparta.communityback.entity.User;
import com.sparta.communityback.entity.UserRoleEnum;
import com.sparta.communityback.repository.UserRepository;
import com.sparta.communityback.service.RedisService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {
    // Header KEY 값
    public static final String ACCESS_TOKEN = "AccessToken";
    public static final String REFRESH_TOKEN = "RefreshToken";
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";

    // accessToken 만료시간
    private final long TOKEN_TIME = 60 * 60 * 1000L; // 한시간

    // refreshToken 만료시간
    private final long REFRESH_TOKEN_TIME = 14 * 24 * 60 * 60 * 1000L; //2주

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final RedisService redisService;
    private final UserRepository userRepository;
    public JwtUtil(RedisService redisService, UserRepository userRepository) {
        this.redisService = redisService;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createAccessToken(String username, UserRoleEnum role) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username) // 사용자 식별자값(ID)
                        .claim(AUTHORIZATION_KEY, role) // 사용자 권한
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }

    public String createRefreshToken(String username){
        Date now = new Date();

        return BEARER_PREFIX +
            Jwts.builder()
                .setSubject(username) // 사용자 식별자값(ID)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_TIME)) // 만료 시간
                .setIssuedAt(now) // 발급일
                .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                .compact();
    }

    // header 에서 Access token 가져오기
    public String getJwtFromHeader(HttpServletRequest request) {
        String accessToken = request.getHeader(ACCESS_TOKEN);
        if (StringUtils.hasText(accessToken) && accessToken.startsWith(BEARER_PREFIX)) {
            return accessToken.substring(7);
        }
        return null;
    }

    // 토큰 검증
    public boolean validateToken(String token, HttpServletRequest req, HttpServletResponse res) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            if(req.getHeader(REFRESH_TOKEN).isEmpty()) {
                log.error("Expired JWT token, 만료된 JWT token 입니다.");
                throw new RuntimeException();
            } else {
                String RefreshToken = req.getHeader(REFRESH_TOKEN);
                String newAccessToken = regenerateAccessToken(RefreshToken);
                res.addHeader(JwtUtil.ACCESS_TOKEN, newAccessToken);
                res.addHeader(JwtUtil.REFRESH_TOKEN, RefreshToken);
                log.info("토큰재발급 성공: {}", newAccessToken);
            }
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    private String regenerateAccessToken(String refreshToken) {
        String redisRefreshToken = redisService.getRefreshToken(refreshToken);
        if(redisRefreshToken == null){
            throw new RuntimeException("저장되지 않은 RefreshToken 입니다.");
        }
        else {
            Optional<User> userOptional = userRepository.findById(Long.valueOf(redisRefreshToken));
            String username = userOptional.get().getUsername();
            UserRoleEnum userRole = userOptional.get().getRole();
            return createAccessToken(username, userRole);
        }
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}