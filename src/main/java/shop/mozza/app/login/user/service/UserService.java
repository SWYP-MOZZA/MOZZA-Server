package shop.mozza.app.login.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import shop.mozza.app.login.user.domain.KakaoOAuth2User;
import shop.mozza.app.login.user.domain.User;
import shop.mozza.app.login.user.repository.UserRepository;


@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

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
