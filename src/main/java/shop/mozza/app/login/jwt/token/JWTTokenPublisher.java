package shop.mozza.app.login.jwt.token;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import shop.mozza.app.login.jwt.JWTUtil;
import shop.mozza.app.login.user.domain.GuestUser;
import shop.mozza.app.login.user.domain.User;
import shop.mozza.app.login.user.dto.UserDto;
import shop.mozza.app.login.user.repository.UserRepository;

import java.util.Random;


@RequiredArgsConstructor
@Service
public class JWTTokenPublisher {

    private final JWTUtil jwtUtil;

    public String IssueGuestToken(User user) {
        String guestToken = jwtUtil.createAccessToken(user.getName(), user.getRole());
        UserDto userDto = UserDto.from(user);
        GuestUser guestUser = new GuestUser(userDto);
        Authentication authToken = new UsernamePasswordAuthenticationToken(guestUser, null, guestUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        return guestToken;
    }

    public String IssueMeetingToken() {
        return jwtUtil.createAccessToken();
    }

}




