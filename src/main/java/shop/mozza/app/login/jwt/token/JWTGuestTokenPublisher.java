package shop.mozza.app.login.jwt.token;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import shop.mozza.app.login.jwt.JWTUtil;
import shop.mozza.app.login.user.domain.GuestUser;
import shop.mozza.app.login.user.domain.User;
import shop.mozza.app.login.user.dto.UserDto;

@RequiredArgsConstructor
public class JWTGuestTokenPublisher {

    private final JWTUtil jwtUtil;



}
