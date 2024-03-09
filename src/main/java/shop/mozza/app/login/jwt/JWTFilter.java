package shop.mozza.app.login.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import shop.mozza.app.global.RefreshTokenService;
import shop.mozza.app.login.oauth2.dto.response.TokenResponse;
import shop.mozza.app.login.user.domain.KakaoOAuth2User;
import shop.mozza.app.login.user.domain.User;
import shop.mozza.app.login.user.dto.UserDto;
import shop.mozza.app.login.user.repository.UserRepository;
import java.util.Date;
import java.io.IOException;



@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {


    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    @Value("${jwt.access-token.expire-length}")
    private int accessTokenValidityInSeconds;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Authorization 헤더에서 토큰 추출
        String authorization = request.getHeader("Authorization");

        authorization = authorization.replace("Bearer ", "");

        //Authorization 헤더 검증
        if (authorization == null || StringUtils.isEmpty(authorization)) {
            log.debug("JWT: Authorization header is missing");
            filterChain.doFilter(request, response);
            return;
        }


        // Refresh Token이 포함되어 있는지 확인
        String refreshToken = request.getHeader("RefreshToken");
        if (refreshToken != null){
            refreshToken = refreshToken.replace("Bearer ", "");
        }


        // AccessToken이 만료되었을 경우에만 Refresh Token을 사용하여 AccessToken을 갱신
        if (jwtUtil.isExpired(authorization)) {
            if (!StringUtils.isEmpty(refreshToken) && refreshTokenService.validateRefreshToken(refreshToken)) {
                String username = jwtUtil.getUsernameFromRefreshToken(refreshToken);
                User user = userRepository.findByName(username);
                if (user != null) {
                    // 새 액세스 토큰 발급
                    String newAccessToken = jwtUtil.createAccessToken(username, user.getRole());
                    // 쿠키에 새 액세스 토큰 추가
                    sendTokenInJsonResponse(response, newAccessToken, accessTokenValidityInSeconds);
                }
            }
        }

        // AccessToken이 유효한 경우, 사용자 정보를 SecurityContext에 설정
        if (jwtUtil.validateToken(authorization)) {
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

    private void sendTokenInJsonResponse(HttpServletResponse response, String newAccessToken, long validityPeriod) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        long expiryDate = System.currentTimeMillis() + validityPeriod;
        Date tokenExpiryDate = new Date(expiryDate);

        TokenResponse tokenResponseDto = new TokenResponse(newAccessToken, tokenExpiryDate);

        // ObjectMapper를 사용하여 DTO를 JSON 문자열로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(tokenResponseDto);
        response.getWriter().write(jsonResponse);
    }
}

