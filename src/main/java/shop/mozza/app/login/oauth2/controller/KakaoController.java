package shop.mozza.app.login.oauth2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import shop.mozza.app.exception.CustomExceptions;
import shop.mozza.app.global.RefreshTokenService;
import shop.mozza.app.login.jwt.JWTUtil;
import shop.mozza.app.login.oauth2.dto.response.KakaoTokenResponse;
import shop.mozza.app.login.oauth2.dto.response.KakaoUserInfoResponse;
import shop.mozza.app.login.oauth2.dto.response.OAuth2LoginResponse;
import shop.mozza.app.login.oauth2.dto.response.refreshTokenResponse;
import shop.mozza.app.login.oauth2.util.KakaoTokenJsonData;
import shop.mozza.app.login.oauth2.util.KakaoUserInfo;
import shop.mozza.app.login.user.domain.KakaoOAuth2User;
import shop.mozza.app.login.user.domain.User;
import shop.mozza.app.login.user.dto.UserDto;
import shop.mozza.app.login.user.repository.UserRepository;
import shop.mozza.app.login.user.service.UserService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class KakaoController {
    private final KakaoTokenJsonData kakaoTokenJsonData;
    private final KakaoUserInfo kakaoUserInfo;
    private final UserService userService;
    private final JWTUtil jwtUtil;

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.access-token.expire-length}")
    private int accessTokenValidityInSeconds;

    @GetMapping("/index")
    public String index() {
        return "loginForm";
    }

    @Description("회원이 소셜 로그인을 마치면 자동으로 실행되는 API입니다. 인가 코드를 이용해 토큰을 받고, 해당 토큰으로 사용자 정보를 조회합니다." +
            "사용자 정보를 이용하여 서비스에 회원가입합니다.")
    @GetMapping("/oauth")
    @ResponseBody
    public ResponseEntity<OAuth2LoginResponse> kakaoOauth(@RequestParam("code") String code) {
        log.info("인가 코드를 이용하여 토큰을 받습니다.");
        KakaoTokenResponse kakaoTokenResponse = kakaoTokenJsonData.getToken(code);
        log.info("토큰에 대한 정보입니다.{}",kakaoTokenResponse);
        KakaoUserInfoResponse userInfo = kakaoUserInfo.getUserInfo(kakaoTokenResponse.getAccess_token());
        log.info("회원 정보 입니다.{}",userInfo);
        User user = userService.createUser(userInfo);
        OAuth2LoginResponse response = userService.getLoginReponse(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/security/token/refresh")
    @ResponseBody
    public ResponseEntity<refreshTokenResponse> issueAccessTokenByRefreshToken(@RequestHeader("Authorization") String authorization,
                                                                               @RequestHeader("RefreshToken") String refreshToken) {


        if (authorization != null){
            authorization = authorization.replace("Bearer ", "");
        }

        //Authorization 헤더 검증
        if (authorization == null || StringUtils.isEmpty(authorization)) {
            throw new CustomExceptions.RefreshTokenException("JWT: Authorization header is missing");
        }

        // Refresh Token이 포함되어 있는지 확인
        if (refreshToken != null){
            refreshToken = refreshToken.replace("Bearer ", "");
        }

        String newAccessToken = null;
        // Refresh Token을 사용하여 AccessToken을 갱신
        String username = jwtUtil.getUsername(refreshToken);
        if (!StringUtils.isEmpty(refreshToken) && refreshTokenService.validateRefreshToken(username, refreshToken)) {
            User user = userRepository.findByName(username);
            if (user != null) {
                // 새 액세스 토큰 발급
                newAccessToken = jwtUtil.createAccessToken(username, user.getRole());
            }
        }


        // AccessToken이 유효한 경우, 사용자 정보를 SecurityContext에 설정
        if (jwtUtil.validateToken(authorization)) {
            String role = jwtUtil.getRole(authorization);
            User user = userRepository.findByName(username);
            if (user != null) {
                UserDto userDto = UserDto.from(user);
                KakaoOAuth2User kakaoOAuth2User = new KakaoOAuth2User(userDto);
                Authentication authToken = new UsernamePasswordAuthenticationToken(kakaoOAuth2User, null, kakaoOAuth2User.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        if (newAccessToken != null){
            return ResponseEntity.ok(refreshTokenResponse.builder()
                    .statusCode(200)
                    .accessToken(newAccessToken)
                    .expiresln(accessTokenValidityInSeconds)
                    .build());
        }
        else {
            throw new CustomExceptions.RefreshTokenException("Access 토큰 재발급 실패");
        }
    }



}
