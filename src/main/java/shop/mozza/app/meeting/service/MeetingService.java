package shop.mozza.app.meeting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mozza.app.meeting.domain.DateTimeInfo;
import shop.mozza.app.meeting.domain.Meeting;
import shop.mozza.app.meeting.repository.DateTimeInfoRepository;
import shop.mozza.app.meeting.repository.MeetingRepository;
import shop.mozza.app.meeting.web.dto.MeetingRequestDto;
import shop.mozza.app.user.domain.User;
import shop.mozza.app.user.domain.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;

    private final DateTimeInfoRepository dateTimeInfoRepository;

    private final UserRepository userRepository;


    // String형의 "2023-10-22"를 LocalDateTime형의 2023-10-22T00:00로 리턴.
    private LocalDateTime stringToDateOnly(String dateString) {
        LocalDateTime localDate = LocalDateTime.parse(dateString);
        return localDate;
    }

    // LocalDateTime형 yyyy-mm-dd을 String "yyyy-mm-dd"형식으로 리턴
    private String dateToString(LocalDate date) {
        return date.toString();
    }

    // String "yyyy-mm-dd"와 String timeList가 주어질 때, LocalDateTime yyyy-mm-ddThh:mm로 return
    private List<LocalDateTime> convertToDateTimeList(String date, List<String> timeList) {
        List<LocalDateTime> dateTimeList = new ArrayList<>();

        for (String time : timeList) {
            String dateTimeString = date + "T" + time; // 날짜와 시간을 합치기
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString); // 문자열을 LocalDateTime으로 변환
            dateTimeList.add(dateTime);
        }
        return dateTimeList;
    }

    private List<String> generateTimeSlots(String startTime, String endTime) {
        List<String> timeSlots = new ArrayList<>();
        int intervalMinutes = 30;

        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        while (!start.isAfter(end)) {
            timeSlots.add(start.toString());
            start = start.plusMinutes(intervalMinutes);
        }
        return timeSlots;
    }

    public void createMeeting(MeetingRequestDto.makeMeetingRequest req) {
        Meeting meeting = Meeting
                .builder()
                .name(req.getName())
                .isDeleted(false)
                .onlyDate(req.getOnlyDate())
                .build();

        meetingRepository.save(meeting);
        List<String> dates = req.getDate();

        if (req.getOnlyDate() == true) {
            createDateMeeting(meeting, dates);
        } else {
            createDateTimeMeeting(meeting,req);
        }
    }

    private void createDateMeeting(Meeting meeting,List<String> dates ) {
        for(String date : dates){
            DateTimeInfo dti = DateTimeInfo
                    .builder()
                    .datetime(stringToDateOnly(date))
                    .meeting(meeting)
                    .build();
            dateTimeInfoRepository.save(dti);
        }
    }


    private void createDateTimeMeeting(Meeting meeting, MeetingRequestDto.makeMeetingRequest req) {
        String startTime = req.getStartTime();
        String endTime = req.getEndTime();
        List<String> times = generateTimeSlots(startTime, endTime);
        List<String> dates = req.getDate();

        for (String date : dates) {
            for (String time : times) {
                LocalDateTime dateTime = LocalDateTime.of(
                        LocalDate.parse(date),
                        LocalTime.parse(time)
                );
                DateTimeInfo dti = DateTimeInfo
                        .builder()
                        .datetime(dateTime)
                        .meeting(meeting)
                        .build();
                dateTimeInfoRepository.save(dti);
            }
        }
    }

    public void addGuest(MeetingRequestDto.guestRequest req){
        String password = req.getPassword();

        // 비밀번호가 빈 문자열인 경우 null로 설정
        if (password != null && password.isEmpty()) {
            password = null;
        }

        User.UserBuilder userBuilder = User.builder()
                .name(req.getName())
                .isMember(false)
                .password(password);

        User user = userBuilder.build();
        userRepository.save(user);

    }


}

