package shop.mozza.app.login.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import shop.mozza.app.global.RefreshTokenService;
import shop.mozza.app.login.user.domain.KakaoOAuth2User;
import shop.mozza.app.login.user.domain.User;
import shop.mozza.app.login.user.dto.UserDto;
import shop.mozza.app.login.user.repository.UserRepository;

import java.io.IOException;



@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;



    private final RefreshTokenService refreshTokenService;


    public JWTFilter(JWTUtil jwtUtil, UserRepository userRepository, RefreshTokenService refreshTokenService) {

        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = null;
        Cookie[] cookies = request.getCookies();
        log.info("JWT : get request");
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("Authorization")) {
                authorization = cookie.getValue();
                break; // 토큰을 찾으면 루프 종료
            }
        }

        //Authorization 헤더 검증
        if (authorization == null) {
            log.info("token null");
            filterChain.doFilter(request, response);
            //조건이 해당되면 메소드 종료 (필수)
            return;
        }


        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if ("Refresh".equals(cookie.getName())) {
                refreshToken = cookie.getValue();
                break; // 리프레시 토큰을 찾으면 루프 종료
            }
        }

        boolean needNewAccessToken = false;
        if (jwtUtil.isExpired(authorization)) {
            if (refreshToken != null && refreshTokenService.validateRefreshToken(refreshToken)) {
                // 리프레시 토큰 유효성 검사
                String username = jwtUtil.getUsernameFromRefreshToken(refreshToken);
                User user = userRepository.findByName(username);
                if (user != null) {
                    // 새 액세스 토큰 발급
                    authorization = jwtUtil.createAccessToken(username, user.getRole()); // 예제로 1시간 설정
                    addCookieToResponse(response, "Authorization", authorization, 60 * 60); // 쿠키에 추가
                    needNewAccessToken = true;
                }
            } else {
                log.info("JWT : Refresh token is invalid or expired");
                filterChain.doFilter(request, response);
                return;
            }
        }

        if (!needNewAccessToken) {
            // 액세스 토큰에서 username과 role 획득
            String username = jwtUtil.getUsername(authorization);
            String role = jwtUtil.getRole(authorization);
            User user = userRepository.findByName(username);
            if (user != null) {
                UserDto userDto = UserDto.from(user);
                KakaoOAuth2User kakaoOAuth2User = new KakaoOAuth2User(userDto);
                Authentication authToken = new UsernamePasswordAuthenticationToken(kakaoOAuth2User, null, kakaoOAuth2User.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void addCookieToResponse(HttpServletResponse response, String name, String value, int maxAgeInSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAgeInSeconds);
        response.addCookie(cookie);
    }


}

