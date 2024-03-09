package shop.mozza.app.login.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import shop.mozza.app.global.RefreshTokenService;
import shop.mozza.app.login.jwt.JWTUtil;
import shop.mozza.app.login.oauth2.dto.response.KakaoUserInfoResponse;
import shop.mozza.app.login.oauth2.dto.response.OAuth2LoginResponse;
import shop.mozza.app.login.user.domain.KakaoOAuth2User;
import shop.mozza.app.login.user.domain.User;
import shop.mozza.app.login.user.dto.UserDto;
import shop.mozza.app.login.user.repository.UserRepository;
import shop.mozza.app.login.oauth2.dto.response.OAuth2Response;

@RequiredArgsConstructor
@Service
public class UserService {


    @Value("${jwt.access-token.expire-length}")
    private long accessTokenValidityInSeconds;

    private final UserRepository userRepository;

    private final JWTUtil jwtUtil;

    private final RefreshTokenService refreshTokenService;


    public User createUser (String username) {
        User exitsUser = userRepository.findByName(username);
        if (exitsUser == null) {
            User newUser = User.builder()
                    .name(username)
                    .isMember(true)
                    .role("USER")
                    .build();
            userRepository.save(newUser);
            return newUser;
        }
        else {
            exitsUser.updateUserName(username);
            userRepository.save(exitsUser);
            return exitsUser;
        }
    }

    public OAuth2LoginResponse getLoginReponse(User user) {

        String refreshToken = jwtUtil.createRefreshToken(user.getName());
        refreshTokenService.saveRefreshToken(user.getName(), refreshToken);


        return OAuth2LoginResponse.builder()
                .statusCode(200)
                .accessToken(jwtUtil.createAccessToken(user.getName(), "USER"))
                .refreshToken(refreshToken)
                .expiresIn(accessTokenValidityInSeconds)
                .userId(user.getId())
                .userName(user.getName())
                .build();

    }


    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "로그인 되지 않았습니다."
            );
        }

        // Assuming the principal can be cast to a KakaoOAuth2User or similar that contains the username
        Object principal = authentication.getPrincipal();
        String username;

        if (principal instanceof KakaoOAuth2User) { // Or any other implementation you use
            username = ((KakaoOAuth2User) principal).getName();
        } else if (principal instanceof String) {
            username = (String) principal; // In case the principal is a String
        } else {
            throw new IllegalStateException("Unexpected principal type");
        }

        User user = userRepository.findByName(username); // Adjust this line to match your repository method

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

//        if (user.getStatus().equals(UserStatus.INACTIVE)) {
//            throw new IllegalArgumentException("해당 사용자는 탈퇴한 사용자입니다.");
//        }
        return user;
    }
}
