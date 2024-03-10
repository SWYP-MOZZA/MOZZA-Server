package shop.mozza.app.login.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.mozza.app.login.user.domain.User;
import shop.mozza.app.login.user.dto.UserDto;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByName(String name);

    User findById(long id);

}
