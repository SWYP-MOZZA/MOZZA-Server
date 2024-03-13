package shop.mozza.app.login.oauth2.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import shop.mozza.app.exception.ResponseMessage;
import shop.mozza.app.global.TokenService;
import shop.mozza.app.login.user.domain.User;
import shop.mozza.app.login.user.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class LogoutController {

    private final TokenService tokenService;
    private final UserService userService;


    @GetMapping("/oauth/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {

            // Redis에서 refresh 토큰을 삭제
            User user  = userService.getCurrentUser();

            String username = user.getName();
            tokenService.deleteRefreshToken(username);


            // redis에 access 토큰을 블랙리스트로 추가
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String accessToken = authorizationHeader.substring(7);
                tokenService.addToBlacklist(accessToken);
            }


            // 현재 인증 정보를 SecurityContext에서 제거하여 로그아웃 처리
            SecurityContextHolder.clearContext();

            // 로그아웃 성공 시 응답 메시지
            Map<String, Object> response = new HashMap<>();
            response.put("StatusCode", 200);
            response.put("ResponseMessage", ResponseMessage.LOGOUT_SUCCESS);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 로그아웃 처리 중 예외 발생 시 응답 메시지
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("StatusCode", 400);
            errorResponse.put("ResponseMessage", ResponseMessage.LOGOUT_FAILED);
            return ResponseEntity.badRequest().body(errorResponse);
        }

    }
}



