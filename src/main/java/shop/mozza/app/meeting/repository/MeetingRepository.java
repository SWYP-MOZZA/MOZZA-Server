package shop.mozza.app.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.mozza.app.meeting.domain.Meeting;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    Meeting findMeetingById(Long id);
}
