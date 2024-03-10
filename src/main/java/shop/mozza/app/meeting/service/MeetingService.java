package shop.mozza.app.meeting.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import shop.mozza.app.login.jwt.token.JWTGuestTokenPublisher;
import shop.mozza.app.login.jwt.token.JWTTokenPublisher;
import shop.mozza.app.exception.CustomExceptions;
import shop.mozza.app.login.oauth2.service.OAuth2UserService;
import shop.mozza.app.login.user.domain.User;
import shop.mozza.app.login.user.repository.UserRepository;
import shop.mozza.app.meeting.domain.Attendee;
import shop.mozza.app.meeting.domain.DateTimeInfo;
import shop.mozza.app.meeting.domain.Meeting;
import shop.mozza.app.meeting.repository.AttendeeRepository;
import shop.mozza.app.meeting.repository.DateTimeInfoRepository;
import shop.mozza.app.meeting.repository.MeetingRepository;
import shop.mozza.app.meeting.web.dto.MeetingRequestDto;
import shop.mozza.app.meeting.web.dto.MeetingResponseDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final DateTimeInfoRepository dateTimeInfoRepository;
    private final UserRepository userRepository;
    private final JWTTokenPublisher jwtTokenPublisher;

    private final AttendeeRepository attendeeRepository;

    @PersistenceContext
    private EntityManager entityManager;


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

    // LocalDateTime yyyy-mm-ddThh:mm이 주어지면 String형의 "yyyy-mm-dd"만 return
    public static String getDateStringFromLocalDateTime(LocalDateTime dateTime) {
        // 날짜를 나타내는 포맷 지정 (yyyy-MM-dd)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // LocalDateTime 객체를 날짜 부분만 나타내는 문자열로 변환
        String dateString = dateTime.format(formatter);
        return dateString;
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

    public Long createMeeting(MeetingRequestDto.makeMeetingRequest req, User user) {
        Meeting meeting = Meeting
                .builder()
                .name(req.getName())
                .isDeleted(false)
                .onlyDate(req.getOnlyDate())
                .creator(user)
                .NumberOfVoter(0)
                .build();

        meetingRepository.save(meeting);
        List<String> dates = req.getDate();

        if (req.getOnlyDate() == true) {
            createDateMeeting(meeting, dates);
        } else {
            createDateTimeMeeting(meeting, req);
        }
        return meeting.getId();
    }

    private void createDateMeeting(Meeting meeting, List<String> dates) {
        for (String date : dates) {
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

    public User addGuest(MeetingRequestDto.guestRequest req) {
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
        return user;


    }


    public void setNotification(MeetingRequestDto.notificationRequest req, Long id) {
        Boolean ableNotification = req.getAbleNotification();
        Integer numberOfVoter = req.getNumberOfVoter();

        Meeting meeting = meetingRepository.findMeetingById(id);

        if (ableNotification) {
            meeting.updateNotificationSettings(numberOfVoter);
        } else {
            meeting.updateNotificationSettings(null);
        }
        meetingRepository.save(meeting);
    }

    public Meeting findMeetingById(Long id) {
        return meetingRepository.findById(id).orElse(null);
    }


    private String findStartDate(List<DateTimeInfo> dateTimeInfos) {
        if (dateTimeInfos.isEmpty()) {
            return null;
        }
        DateTimeInfo earliestDateTimeInfo = dateTimeInfos.get(0);
        for (DateTimeInfo dateTimeInfo : dateTimeInfos) {
            if (dateTimeInfo.getDatetime().isBefore(earliestDateTimeInfo.getDatetime())) {
                earliestDateTimeInfo = dateTimeInfo;
            }
        }
        return earliestDateTimeInfo.getDatetime().toLocalDate().toString();
    }

    private String findEndDate(List<DateTimeInfo> dateTimeInfos) {
        if (dateTimeInfos.isEmpty()) {
            return null;
        }
        DateTimeInfo latestDateTimeInfo = dateTimeInfos.get(0);
        for (DateTimeInfo dateTimeInfo : dateTimeInfos) {
            if (dateTimeInfo.getDatetime().isAfter(latestDateTimeInfo.getDatetime())) {
                latestDateTimeInfo = dateTimeInfo;
            }
        }
        return latestDateTimeInfo.getDatetime().toLocalDate().toString();
    }

    private String findStartTime(List<DateTimeInfo> dateTimeInfos) {
        if (dateTimeInfos.isEmpty()) {
            return null;
        }
        DateTimeInfo earliestDateTimeInfo = dateTimeInfos.get(0);
        for (DateTimeInfo dateTimeInfo : dateTimeInfos) {
            if (dateTimeInfo.getDatetime().isBefore(earliestDateTimeInfo.getDatetime())) {
                earliestDateTimeInfo = dateTimeInfo;
            }
        }
        return earliestDateTimeInfo.getDatetime().toLocalTime().toString();
    }

    private String findEndTime(List<DateTimeInfo> dateTimeInfos) {
        if (dateTimeInfos.isEmpty()) {
            return null;
        }
        DateTimeInfo latestDateTimeInfo = dateTimeInfos.get(0);
        for (DateTimeInfo dateTimeInfo : dateTimeInfos) {
            if (dateTimeInfo.getDatetime().isAfter(latestDateTimeInfo.getDatetime())) {
                latestDateTimeInfo = dateTimeInfo;
            }
        }
        return latestDateTimeInfo.getDatetime().toLocalTime().toString();
    }

    private List<String> findAttendee(List<DateTimeInfo> dateTimeInfos) {
        List<String> attendees = new ArrayList<>();
        for (DateTimeInfo dateTimeInfo : dateTimeInfos) {
            List<String> attendeesForDateTimeInfo = findAttendeesByDateTimeInfo(dateTimeInfo);
            attendees.addAll(attendeesForDateTimeInfo);
        }
        return attendees;
    }

    private List<String> findAttendeesByDateTimeInfo(DateTimeInfo dateTimeInfo) {
        List<String> attendeeNames = entityManager.createQuery(
                        "SELECT a.user.name FROM Attendee a WHERE a.dateTimeInfo = :dateTimeInfo", String.class)
                .setParameter("dateTimeInfo", dateTimeInfo)
                .getResultList();
        return attendeeNames;
    }


    public MeetingResponseDto.SummaryResponse createSummaryResponse(Meeting meeting) {
        List<DateTimeInfo> dateTimeInfos = meeting.getDateTimeInfos();
        MeetingResponseDto.SummaryResponse response = MeetingResponseDto.SummaryResponse
                .builder()
                .meetingId(meeting.getId())
                .name(meeting.getName())
                .startDate(findStartDate(dateTimeInfos))
                .endDate(findEndDate(dateTimeInfos))
                .startTime(findStartTime(dateTimeInfos))
                .endTime(findEndTime(dateTimeInfos))
                .numberOfVoter(meeting.getNumberOfVoter())
                .attendee(findAttendee(dateTimeInfos))
                .build();
        return response;

    }

    private List<String> findAllDates(List<DateTimeInfo> dateTimeInfos, Long meetingId) {
        Set<String> dateSet = new HashSet<>();
        for (DateTimeInfo dateTimeInfo : dateTimeInfos) {
            if (dateTimeInfo.getMeeting().getId().equals(meetingId)) {
                dateSet.add(dateTimeInfo.getDatetime().toLocalDate().toString());
            }
        }
        return new ArrayList<>(dateSet);
    }
    public MeetingResponseDto.ChoiceResponse createChoiceResponse(Meeting meeting) {
        List<DateTimeInfo> dateTimeInfos = meeting.getDateTimeInfos();
        MeetingResponseDto.ChoiceResponse response = MeetingResponseDto.ChoiceResponse
                .builder()
                .meetingId(meeting.getId())
                .name(meeting.getName())
                .date(findAllDates(dateTimeInfos, meeting.getId()))
                .startTime(findStartTime(dateTimeInfos))
                .endTime(findEndTime(dateTimeInfos))
                .build();
        return response;
    }


    public void submitMeetingDate(User user,Long id, List<MeetingRequestDto.DateSubmitRequest> dateRequests) {
        Optional<Meeting> optionalMeeting = meetingRepository.findById(id);
        if (optionalMeeting.isPresent()) {
            Meeting meeting = optionalMeeting.get();

            for (MeetingRequestDto.DateSubmitRequest dateRequest : dateRequests) {
                String date = dateRequest.getDate();
                boolean isActive = dateRequest.isActive();
                Optional<DateTimeInfo> optionalDateTimeInfo = meeting.getDateTimeInfos()
                        .stream()
                        .filter(dateTimeInfo -> getDateStringFromLocalDateTime(dateTimeInfo.getDatetime()).equals(date))
                        .findFirst();

                if (optionalDateTimeInfo.isPresent() && isActive) {
                    DateTimeInfo dateTimeInfo = optionalDateTimeInfo.get();

                    Attendee attendee = Attendee
                            .builder()
                            .dateTimeInfo(dateTimeInfo)
                            .user(user)
                            .build();
                    attendeeRepository.save(attendee);
                }
            }
        }
    }
}



