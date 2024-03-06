package shop.mozza.app.login.oauth2.service;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import shop.mozza.app.global.RefreshTokenService;
import shop.mozza.app.login.jwt.JWTUtil;
import shop.mozza.app.login.user.domain.KakaoOAuth2User;
import shop.mozza.app.login.user.domain.User;
import shop.mozza.app.login.user.repository.UserRepository;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException, IOException {

        //OAuth2User
        KakaoOAuth2User customUserDetails = (KakaoOAuth2User) authentication.getPrincipal();
        String username = customUserDetails.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();
        log.debug("OAuth : 인증 성공 " + username);

        Long userId = userRepository.findByName(username).getId();


        // Access Token과 Refresh Token 생성
        String accessToken = jwtUtil.createAccessToken(username, role);
        String refreshToken = jwtUtil.createRefreshToken(username);

        // Refresh Token을 Redis에 저장
        refreshTokenService.saveRefreshToken(username, refreshToken);

        // 토큰을 쿠키에 추가
        response.addCookie(createCookie("Authorization", accessToken));
        response.addCookie(createCookie("RefreshToken", refreshToken));

        response.sendRedirect("http://localhost:3000/");

    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}

