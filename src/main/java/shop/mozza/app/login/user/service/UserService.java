package shop.mozza.app.login.user.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import shop.mozza.app.global.TokenService;
import shop.mozza.app.login.jwt.JWTUtil;
import shop.mozza.app.login.oauth2.dto.response.KakaoUserInfoResponse;
import shop.mozza.app.login.oauth2.dto.response.OAuth2LoginResponse;
import shop.mozza.app.login.user.domain.KakaoOAuth2User;
import shop.mozza.app.login.user.domain.User;
import shop.mozza.app.login.user.repository.UserRepository;

import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class UserService {


    @Value("${jwt.access-token.expire-length}")
    private long accessTokenValidityInSeconds;

    private final UserRepository userRepository;

    private final JWTUtil jwtUtil;

    private final TokenService tokenService;


    public User createUser (KakaoUserInfoResponse userInfo) {
        String username = userInfo.getKakao_account().getProfile().getNickname();
        String userEmail = userInfo.getKakao_account().getEmail();
        Long oauthId = userInfo.getId();
        Optional<User> exitsUser = userRepository.findByOauthId(oauthId);
        if (exitsUser.isEmpty()) {
            User newUser = User.builder()
                    .name(username)
                    .isMember(true)
                    .role("USER")
                    .email(userEmail)
                    .oauthId(oauthId)
                    .build();

            userRepository.save(newUser);
            return newUser;
        }
        else {
            exitsUser.get().updateUserName(username);
            exitsUser.get().updateUserEmail(userEmail);
            userRepository.save(exitsUser.get());
            return exitsUser.get();
        }
    }

    public OAuth2LoginResponse getLoginReponse(User user) {

        String refreshToken = jwtUtil.createRefreshToken(user.getName(), "USER", user.getId());
        tokenService.saveRefreshToken(user.getId(), refreshToken);


        return OAuth2LoginResponse.builder()
                .statusCode(200)
                .accessToken(jwtUtil.createAccessToken(user.getName(), "USER", user.getId()))
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
        if (principal.equals("anonymousUser")) {

            Random rand = new Random();
            // 100000(10^5) 이상 999999(10^6 - 1) 이하의 랜덤 정수 생성
            int randomNum = rand.nextInt(900000) + 100000;


            return User.builder()
                    .role("anonymousUser")
                    .isMember(false)
                    .name("anonymousUser"+ String.valueOf(randomNum))
                    .build();

        }
        Long userId;


        if (principal instanceof KakaoOAuth2User) { // Or any other implementation you use
            userId = ((KakaoOAuth2User) principal).getID();
        }
        else {
            throw new IllegalStateException("Unexpected principal type");
        }

        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new); // Adjust this line to match your repository method

        if (user == null) {
            throw new UsernameNotFoundException("User not found with userId: " + userId.toString());
        }

        return user;
    }
}
