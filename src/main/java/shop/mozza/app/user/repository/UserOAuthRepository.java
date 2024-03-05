package shop.mozza.app.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import shop.mozza.app.user.domain.User;
import shop.mozza.app.user.domain.UserOAuth;

import java.util.Optional;

public interface UserOAuthRepository extends JpaRepository<UserOAuth, Long> {

    UserOAuth findByUser(User user);


}
