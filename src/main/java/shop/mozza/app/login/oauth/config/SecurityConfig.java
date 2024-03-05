package shop.mozza.app.login.oauth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;


import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import shop.mozza.app.login.oauth.service.OAuth2SuccessHandler;
import shop.mozza.app.login.oauth.service.OAuth2UserService;

//@Configuration
//@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //csrf disable
                .csrf((auth) -> auth.disable())

                //From 로그인 방식 disable
                .formLogin((auth) -> auth.disable())

                //HTTP Basic 인증 방식 disable
                .httpBasic((auth) -> auth.disable())

                // oauth2
                .oauth2Login((oauth2) -> oauth2
                        .loginPage("/login")
                        .successHandler(oAuth2SuccessHandler)
                        .userInfoEndpoint((userInfoEndpointConfig ->
                                userInfoEndpointConfig.userService(oAuth2UserService))))
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/oauth2/**", "/login/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout((logoutConfig) ->
                        logoutConfig.logoutUrl("/logout"));


        return http.build();
    }

}
