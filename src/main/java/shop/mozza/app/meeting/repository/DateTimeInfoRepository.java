package shop.mozza.app.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.mozza.app.meeting.domain.DateTimeInfo;
import shop.mozza.app.meeting.domain.Meeting;

import java.util.List;

public interface DateTimeInfoRepository extends JpaRepository<DateTimeInfo,Long> {
    List<DateTimeInfo> findByMeeting(Meeting meeting);
    List<DateTimeInfo> findByMeetingAndIsConfirmed(Meeting meeting, Boolean isConfirmed);
}
