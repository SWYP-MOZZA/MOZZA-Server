package shop.mulmagi.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.mulmagi.app.domain.Test;

public interface TestRepository extends JpaRepository<Test, Long> {
}
