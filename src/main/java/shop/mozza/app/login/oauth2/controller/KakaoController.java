package shop.mozza.app.login.oauth2.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
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
import org.springframework.web.bind.annotation.*;
import shop.mozza.app.exception.CustomExceptions;
import shop.mozza.app.global.TokenService;
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
import shop.mozza.app.meeting.domain.Meeting;
import shop.mozza.app.meeting.repository.MeetingRepository;

@Controller
@RequiredArgsConstructor
@Slf4j
public class KakaoController {
    private final KakaoTokenJsonData kakaoTokenJsonData;
    private final KakaoUserInfo kakaoUserInfo;
    private final UserService userService;
    private final JWTUtil jwtUtil;

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final MeetingRepository meetingRepository;

    @Value("${jwt.access-token.expire-length}")
    private int accessTokenValidityInSeconds;


    @Description("회원이 소셜 로그인을 마치면 자동으로 실행되는 API입니다. 인가 코드를 이용해 토큰을 받고, 해당 토큰으로 사용자 정보를 조회합니다." +
            "사용자 정보를 이용하여 서비스에 회원가입합니다.")
    @GetMapping("/oauth")
    @ResponseBody
    public ResponseEntity<OAuth2LoginResponse> kakaoOauth(@RequestParam("code") String code, HttpSession session) {
        log.info("인가 코드를 이용하여 토큰을 받습니다.");
        KakaoTokenResponse kakaoTokenResponse = kakaoTokenJsonData.getToken(code);
        log.info("토큰에 대한 정보입니다.{}",kakaoTokenResponse);
        KakaoUserInfoResponse userInfo = kakaoUserInfo.getUserInfo(kakaoTokenResponse.getAccess_token());
        log.info("회원 정보 입니다.{}",userInfo);
        User user = userService.createUser(userInfo);
        OAuth2LoginResponse response = userService.getLoginReponse(user);

        Long meetingId = (Long) session.getAttribute("meetingId");
        if (meetingId != null) {

            Meeting meeting = meetingRepository.findMeetingById(meetingId);
            meeting.updateCreator(user);
            log.info("세션에서 꺼낸 모임 ID: {}", meetingId);
            log.info("모임 ID: {} 모임장 :{}", meetingId, user.getName());
            // 여기서 meetingId와 관련된 추가 로직을 수행할 수 있습니다.
            // 예: 사용자를 해당 모임의 페이지로 리다이렉트하기
            meetingRepository.save(meeting);
        } else {
            log.info("세션에 모임 ID가 없습니다.");
            // 세션에 모임 ID가 없는 경우의 처리 로직
        }

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

        // 블랙리스트 토큰인 경우
        if (tokenService.isTokenBlacklisted(authorization)) {
            throw new CustomExceptions.RefreshTokenException("JWT: 로그아웃 처리된 토큰");
        }

        String newAccessToken = null;
        // Refresh Token을 사용하여 AccessToken을 갱신
        Long id = jwtUtil.getId(refreshToken);
        if (!StringUtils.isEmpty(refreshToken) && tokenService.validateRefreshToken(id, refreshToken)) {
            User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
            if (user != null) {
                // 새 액세스 토큰 발급
//                newAccessToken = jwtUtil.createAccessToken(id, user.getRole(), user.getId());
            }
        }


        // AccessToken이 유효한 경우, 사용자 정보를 SecurityContext에 설정
        if (jwtUtil.validateToken(authorization)) {
            User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
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
