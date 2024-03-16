package shop.mozza.app.login.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.mozza.app.login.user.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByName(String name);

    Optional<User> findById(Long id);


}
