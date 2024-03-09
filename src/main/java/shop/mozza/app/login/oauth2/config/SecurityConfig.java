package shop.mozza.app.login.oauth2.config;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import shop.mozza.app.global.RefreshTokenService;
import shop.mozza.app.login.jwt.JWTFilter;
import shop.mozza.app.login.jwt.JWTUtil;
import shop.mozza.app.login.oauth2.service.OAuth2SuccessHandler;
import shop.mozza.app.login.oauth2.service.OAuth2UserService;
import shop.mozza.app.login.user.repository.UserRepository;

import java.util.Arrays;
import java.util.Collections;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    private final RefreshTokenService refreshTokenService;

    public SecurityConfig(OAuth2UserService oAuth2UserService, OAuth2SuccessHandler customSuccessHandler, JWTUtil jwtUtil, UserRepository userRepository, RefreshTokenService refreshTokenService) {

        this.oAuth2UserService = oAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();
                        configuration.setAllowedOrigins(Collections.singletonList("*")); // 모든 출처 허용
                        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 필요한 메소드만 명시하거나 모두 허용
                        configuration.setAllowCredentials(true); // 크로스-도메인 쿠키 허용
                        configuration.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더 허용
                        configuration.setMaxAge(3600L); // 사전 요청 결과의 최대 캐시 시간 설정
                        configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization")); // 클라이언트에 노출될 특정 헤더 설정

                        return configuration;
                    }
                }));


        //csrf disable
        http
                .csrf((auth) -> auth.disable());

        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //HTTP Basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());


        //JWTFilter 추가
        http
                .addFilterAfter(new JWTFilter(jwtUtil,userRepository,refreshTokenService), OAuth2LoginAuthenticationFilter.class);
//        //oauth2
//        http
//                .oauth2Login((oauth2) -> oauth2
//                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
//                                .userService(oAuth2UserService))
//                        .successHandler(customSuccessHandler)
//                );

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/guest").permitAll()
                        .requestMatchers("/meeting/create").permitAll()
                        .requestMatchers("/oauth").permitAll()
                        .anyRequest().authenticated());

        //세션 설정 : STATELESS
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}





