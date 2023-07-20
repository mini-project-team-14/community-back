package com.sparta.communityback.config;

import com.sparta.communityback.jwt.JwtUtil;
import com.sparta.communityback.security.AuthExceptionFilter;
import com.sparta.communityback.security.JwtAuthenticationFilter;
import com.sparta.communityback.security.JwtAuthorizationFilter;
import com.sparta.communityback.security.UserDetailsServiceImpl;
import com.sparta.communityback.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity // Spring Security 지원을 가능하게 함
@RequiredArgsConstructor
public class WebSecurityConfig implements WebMvcConfigurer {

    private final JwtUtil jwtUtil;
    private final CorsConfig corsConfig;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final RedisService redisService;
    private final static String LOGIN_URL = "/api/user/login";

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
//        config.addAllowedOriginPattern("*");
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addExposedHeader("*"); // https://iyk2h.tistory.com/184?category=875351 // 헤더값 보내줄 거 설정.


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, redisService);
        filter.setFilterProcessesUrl(LOGIN_URL);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> {
            web.ignoring()
                    .requestMatchers("/api/user/**");
//                    .requestMatchers(HttpMethod.GET, "/api/boards/**");
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf(AbstractHttpConfigurer::disable);

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                                .requestMatchers("/api/user/**").permitAll() // '/api/user/'로 시작하는 요청 모두 접근 허가
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Preflight Request 허용해주기
                                .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );

//        http.formLogin((formLogin) ->
//                formLogin
//                        .loginPage(LOGIN_URL).permitAll()
//        );

        http.cors(Customizer.withDefaults());
//        // corsConfig setting -------------------------------
//        http.addFilterBefore(corsConfig.corsFilter(), JwtAuthenticationFilter.class); // SPRING 3.0

        // 필터 관리
//        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
//        http.addFilterBefore(authExceptionFilter(), JwtAuthorizationFilter.class);

        http.addFilterBefore(new JwtAuthorizationFilter(jwtUtil, userDetailsService, redisService), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new AuthExceptionFilter(), JwtAuthorizationFilter.class);

        return http.build();
    }
}