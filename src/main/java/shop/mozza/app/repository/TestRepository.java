package shop.mozza.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.mozza.app.domain.Test;

public interface TestRepository extends JpaRepository<Test, Long> {
}
