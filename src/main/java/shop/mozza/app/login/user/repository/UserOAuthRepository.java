package shop.mozza.app.login.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.mozza.app.login.user.domain.User;
import shop.mozza.app.login.user.domain.UserOAuth;

public interface UserOAuthRepository extends JpaRepository<UserOAuth, Long> {

    UserOAuth findByUser(User user);


}
