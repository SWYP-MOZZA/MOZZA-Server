package shop.mozza.app.user.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.mozza.app.user.domain.User;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByName(String name);
}
