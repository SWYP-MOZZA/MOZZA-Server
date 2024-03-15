package shop.mozza.app.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.mozza.app.login.user.domain.User;
import shop.mozza.app.meeting.domain.Meeting;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    Meeting findMeetingById(Long id);

    List<Meeting> findMeetingsByCreator(User user);

}
