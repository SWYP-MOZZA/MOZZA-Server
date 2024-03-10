package shop.mozza.app.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.mozza.app.meeting.domain.Attendee;

public interface AttendeeRepository extends JpaRepository<Attendee, Long> {
}
