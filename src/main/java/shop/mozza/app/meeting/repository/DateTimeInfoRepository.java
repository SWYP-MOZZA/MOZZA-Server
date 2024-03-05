package shop.mozza.app.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.mozza.app.meeting.domain.DateTimeInfo;

public interface DateTimeInfoRepository extends JpaRepository<DateTimeInfo,Long> {
}
