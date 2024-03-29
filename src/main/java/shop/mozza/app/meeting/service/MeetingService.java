package shop.mozza.app.meeting.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import shop.mozza.app.exception.CustomExceptions;
import shop.mozza.app.exception.ResponseMessage;
import shop.mozza.app.login.jwt.token.JWTTokenPublisher;
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
import java.util.stream.Collectors;


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

    private boolean multipleSubmit = false;

    @PersistenceContext
    private EntityManager entityManager;


    // String형의 "2023-10-22"를 LocalDateTime형의 2023-10-22T00:00로 리턴.
    private LocalDateTime stringToDateTimeOnly(String dateString) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateString);
        return localDateTime;
    }

    private LocalDate stringToDateOnly(String dateString) {
        LocalDate localDate = LocalDate.parse(dateString);
        return localDate;
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

    public static String getDateStringFromLocalDate(LocalDate date) {
        // 날짜를 나타내는 포맷 지정 (yyyy-MM-dd)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // LocalDate 객체를 날짜 부분만 나타내는 문자열로 변환
        String dateString = date.format(formatter);
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

    public MeetingResponseDto.CreateResponse createMeeting(MeetingRequestDto.makeMeetingRequest req, Optional<User> user) {
        // Meeting 객체 빌더를 사용하여 공통 속성 설정
        Meeting.MeetingBuilder meetingBuilder = Meeting.builder()
                .name(req.getName())
                .isDeleted(false)
                .onlyDate(req.getOnlyDate())
                .isConfirmed(false)
                .NumberOfVoter(0);

        // user가 존재하면 creator 설정
        user.ifPresent(meetingBuilder::creator);
        Meeting meeting = meetingBuilder.build();
        meetingRepository.save(meeting);
        List<String> dates = req.getDate();

        if (req.getOnlyDate()) {
            createDateMeeting(meeting, dates);
        } else {
            createDateTimeMeeting(meeting, req);
        }
        return MeetingResponseDto.CreateResponse.builder()
                .ResponseMessage(ResponseMessage.MAKE_MEETING_SUCCESS)
                .meetingId(meeting.getId())
                .accessToken(jwtTokenPublisher.IssueMeetingToken())
                .URL( "www.mozza.com/meeting/" + meeting.getId()+"/short")
                .statusCode(200)
                .build();
    }

    private void createDateMeeting(Meeting meeting, List<String> dates) {
        for (String date : dates) {
            DateTimeInfo dti = DateTimeInfo
                    .builder()
                    .datetime(stringToDateOnly(date).atStartOfDay())
                    .meeting(meeting)
                    .isConfirmed(false)
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
                        .isConfirmed(false)
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
        }else if (userRepository.findByNameAndPassword(req.getName(),req.getPassword()).isPresent()) {
            multipleSubmit = true;
            return userRepository.findByNameAndPassword(req.getName(), req.getPassword()).get();
        }

        User.UserBuilder userBuilder = User.builder()
                .name(req.getName())
                .isMember(false)
                .password(password)
                .role("GUEST");
        User user = userBuilder.build();
        userRepository.save(user);
        return user;

    }
    public void updateCreator(Long meetingId, User user) {
        if (meetingId != null) {
            Meeting meeting = meetingRepository.findMeetingById(meetingId);
            meeting.updateCreator(user);
            meetingRepository.save(meeting);
        } else {
            log.info("모임 생성자가 아닌 모임 참가자입니다.");
        }
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
        return meetingRepository.findById(id)
                .orElseThrow(() -> new CustomExceptions.MeetingNotFoundException(id+ " 모임이 존재하지 않습니다."));
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
        Set<Long> attendees = new HashSet<>();
        for (DateTimeInfo dateTimeInfo : dateTimeInfos) {
            List<Long> attendeesForDateTimeInfo = findAttendeesByDateTimeInfo(dateTimeInfo);
            attendees.addAll(attendeesForDateTimeInfo);
        }
        List<Long> attendeeIdList = new ArrayList<>(attendees);
        List<String> attendeeNamesList = new ArrayList<>();
        for (Long id : attendeeIdList) {
            attendeeNamesList.add(userRepository.findById(id).get().getName());
        }
        return attendeeNamesList;
    }

    private List<Long> findAttendeesByDateTimeInfo(DateTimeInfo dateTimeInfo) {
        return entityManager.createQuery(
                        "SELECT a.user.id FROM Attendee a WHERE a.dateTimeInfo = :dateTimeInfo", Long.class)
                .setParameter("dateTimeInfo", dateTimeInfo)
                .getResultList();
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

    private void updateAttendee(User user, DateTimeInfo dateTimeInfo){
        Attendee attendee = Attendee
                .builder()
                .dateTimeInfo(dateTimeInfo)
                .user(user)
                .build();
        attendeeRepository.save(attendee);
    }
    public void submitMeetingDate(User user, Long id, List<MeetingRequestDto.DateSubmitRequest> dateRequests) {
        Optional<Meeting> optionalMeeting = meetingRepository.findById(id);
        if (optionalMeeting.isPresent()) {
            Meeting meeting = optionalMeeting.get();
            removeExistingAttendee(user, meeting);
            processDateRequests(dateRequests, meeting, user);
            if (!multipleSubmit) {
                meeting.addSubmitCount();
            }
        } else {
            throw new CustomExceptions.MeetingNotFoundException("해당 id의 meeting이 없습니다.");
        }
    }

    public void submitMeetingDateTime(User user, Long id, Map<String, List<MeetingRequestDto.TimeSlot>> dateTimeRequests) {
        Optional<Meeting> optionalMeeting = meetingRepository.findById(id);
        if (!optionalMeeting.isPresent()) {
            throw new CustomExceptions.MeetingNotFoundException("모임을 찾을 수 없습니다.");
        }
        Meeting meeting = optionalMeeting.get();
        removeExistingAttendee(user, meeting);
        processDateTimeRequests(dateTimeRequests, meeting, user);
        if (!multipleSubmit) {
            meeting.addSubmitCount();
        }
    }

    private void removeExistingAttendee(User user, Meeting meeting) {
        List<DateTimeInfo> dateTimeInfos = dateTimeInfoRepository.findByMeeting(meeting);
        for (DateTimeInfo dateTimeInfo : dateTimeInfos){
            List<Attendee> existingAttendees = attendeeRepository.findAttendeesByDateTimeInfoAndAndUser(dateTimeInfo, user);
            existingAttendees.forEach(attendeeRepository::delete);
        }

    }

    private void processDateRequests(List<MeetingRequestDto.DateSubmitRequest> dateRequests, Meeting meeting, User user) {
        for (MeetingRequestDto.DateSubmitRequest dateRequest : dateRequests) {
            String date = dateRequest.getDate();
            boolean isActive = dateRequest.getIsActive();
            Optional<DateTimeInfo> optionalDateTimeInfo = meeting.getDateTimeInfos()
                    .stream()
                    .filter(dateTimeInfo -> getDateStringFromLocalDateTime(dateTimeInfo.getDatetime()).equals(date))
                    .findFirst();

            if (optionalDateTimeInfo.isPresent() && isActive) {
                DateTimeInfo dateTimeInfo = optionalDateTimeInfo.get();
                updateAttendee(user, dateTimeInfo);
            }
        }
    }

    private void processDateTimeRequests(Map<String, List<MeetingRequestDto.TimeSlot>> dateTimeRequests, Meeting meeting, User user) {
        for (Map.Entry<String, List<MeetingRequestDto.TimeSlot>> entry : dateTimeRequests.entrySet()) {
            String date = entry.getKey();
            List<MeetingRequestDto.TimeSlot> timeSlots = entry.getValue();

            for (MeetingRequestDto.TimeSlot timeSlot : timeSlots) {
                String time = timeSlot.getTime();
                boolean isActive = timeSlot.getIsActive();
                LocalDateTime dateTime = LocalDateTime.parse(date + "T" + time);

                Optional<DateTimeInfo> optionalDateTimeInfo = meeting.getDateTimeInfos()
                        .stream()
                        .filter(dateTimeInfo -> dateTimeInfo.getDatetime().equals(dateTime))
                        .findFirst();

                if (optionalDateTimeInfo.isPresent() && isActive) {
                    DateTimeInfo dateTimeInfo = optionalDateTimeInfo.get();
                    updateAttendee(user, dateTimeInfo);
                }
            }
        }
    }

    //User 정보로 모든 meeting을 찾아오는 함수
    public List<Meeting> findMeetingsByUser(User user) {
        return meetingRepository.findMeetingsByCreator(user);
    }


    // confirmed meeting과 in progress meeting 모두 mapped한 후 가져와서 response를 만들어주는 함수
    public MeetingResponseDto.AllMeetingResponseDto findAllmeetings(User user) {
        List<Meeting> meetings = findMeetingsByUser(user);
        List<MeetingResponseDto.ConfirmedMeetingInfo> confirmedMeetings = mapToConfirmedMeetingInfo(meetings);
        List<MeetingResponseDto.InprogressMeetingInfo> inProgress = mapToInProgressMeetingInfo(meetings);

        return MeetingResponseDto.AllMeetingResponseDto.builder()
                .StatusCode(200)
                .ConfirmedMeetings(confirmedMeetings)
                .InProgress(inProgress)
                .ResponseMessage(ResponseMessage.GET_ALL_MEETING_SUCCESS)
                .build();

    }

    private List<MeetingResponseDto.ConfirmedMeetingInfo> mapToConfirmedMeetingInfo(List<Meeting> meetings) {
        return meetings.stream()
                .filter(meeting -> meeting.getIsConfirmed() != null && meeting.getIsConfirmed())
                .map(this::mapToMeetingInfo)
                .collect(Collectors.toList());
    }

    private List<MeetingResponseDto.InprogressMeetingInfo> mapToInProgressMeetingInfo(List<Meeting> meetings) {
        return meetings.stream()
                .filter(meeting -> !meeting.getIsConfirmed())
                .map(this::mapToInProgressMeetingInfo)
                .collect(Collectors.toList());
    }

    public static String getTimeAsString(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new CustomExceptions.Exception("연결된 localDateTime이 없습니다.");
        }
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    // Meeting을 MeetingInfoDTO로 mapping하는 함수
    private MeetingResponseDto.ConfirmedMeetingInfo mapToMeetingInfo(Meeting meeting) {
        return MeetingResponseDto.ConfirmedMeetingInfo.builder()
                .meetingId(meeting.getId())
                .meetingName(meeting.getName())
                .confirmedDate(getDateStringFromLocalDateTime(meeting.getConfirmedStartDateTime()))
                .confirmedTime(MeetingResponseDto.TimeInfo.builder()
                        .startTime(getTimeAsString(meeting.getConfirmedStartDateTime()))
                        .endTime(getTimeAsString(meeting.getConfirmedEndDateTime()))
                        .build())
                .submitUserNumber(meeting.getNumberOfVoter())
                .createdAt(meeting.getCreatedAt())
                .build();
    }

    private MeetingResponseDto.InprogressMeetingInfo mapToInProgressMeetingInfo(Meeting meeting) {
        return MeetingResponseDto.InprogressMeetingInfo.builder()
                .meetingId(meeting.getId())
                .meetingName(meeting.getName())
                .submitUserNumber(meeting.getNumberOfVoter())
                .createdAt(meeting.getCreatedAt())
                .build();
    }

    // String 날짜 + 시간을 LocalDateTime으로 바꾸는 함수
    private static LocalDateTime convertStringToDateTime(String date, String time) {
        LocalDateTime dateTime = LocalDateTime.of(
                LocalDate.parse(date),
                LocalTime.parse(time)
        );
        return dateTime;
    }

    // DateTimeInfo의 confirmed만 바꾸는 함수
    private void confirmDateTimeInfo(Meeting meeting, LocalDateTime start, LocalDateTime end) {
        List<DateTimeInfo> dateTimeInfos = dateTimeInfoRepository.findByMeeting(meeting);

        if (dateTimeInfos != null) {
            for (DateTimeInfo dateTimeInfo : dateTimeInfos) {
                if (dateTimeInfo != null) {
                    if (dateTimeInfo.getDatetime().toLocalTime().equals(start))
                        dateTimeInfo.updateIsConfirmed(true);
                    else if (dateTimeInfo.getDatetime().toLocalTime().equals(end))
                        dateTimeInfo.updateIsConfirmed(true);
                    else if (dateTimeInfo.getDatetime().toLocalTime().isAfter(start.toLocalTime()) &&
                            dateTimeInfo.getDatetime().toLocalTime().isBefore(end.toLocalTime())) {
                        dateTimeInfo.updateIsConfirmed(true);
                    }
                }
            }
        }
    }


    // meeting의 confirmed 관련 내용을 업데이트 하고 response 내용에 맞게 mapping하는 함수
    public MeetingResponseDto.confirmResponse confirmMeeting(Meeting meeting, MeetingRequestDto.confirmDateTimeRequest request) {

        String date = request.getConfirmedDate();
        LocalDateTime startDateTime = convertStringToDateTime(date, request.getConfirmedStartTime());
        LocalDateTime endDateTime = convertStringToDateTime(date, request.getConfirmedEndTime());

        confirmDateTimeInfo(meeting, startDateTime, endDateTime);
        meeting.updateIsConfirmed(true, startDateTime, endDateTime);

        return MeetingResponseDto.confirmResponse
                .builder()
                .id(meeting.getId())
                .createdAt(meeting.getCreatedAt())
                .numberOfSubmit(meeting.getNumberOfVoter())
                .confirmedDate(date)
                .confirmedStartTime(request.getConfirmedStartTime())
                .confirmedEndTime(request.getConfirmedEndTime())
                .StatusCode(200)
                .ResponseMessage(ResponseMessage.CONFIRM_MEETING_SUCCESS)
                .build();

    }

    public MeetingResponseDto.confirmDateResponse confirmDateMeeting(Meeting meeting, MeetingRequestDto.confirmDateRequest request) {
        LocalDateTime startDateTime = stringToDateOnly(request.getConfirmedStartDate()).atStartOfDay();
        LocalDateTime endDateTime = stringToDateOnly(request.getConfirmedEndDate()).atStartOfDay();

        confirmDateTimeInfo(meeting, startDateTime, endDateTime);
        meeting.updateIsConfirmed(true, startDateTime, endDateTime);

        return MeetingResponseDto.confirmDateResponse
                .builder()
                .id(meeting.getId())
                .createdAt(meeting.getCreatedAt())
                .numberOfSubmit(meeting.getNumberOfVoter())
                .confirmedStartDate(request.getConfirmedStartDate())
                .confirmedEndDate(request.getConfirmedEndDate())
                .StatusCode(200)
                .ResponseMessage(ResponseMessage.CONFIRM_MEETING_SUCCESS)
                .build();



    }

    // ------------------confirm meeting 끝, get meeting details 시작 ------------------------

    // time range를 만드는 함수, startTime과 endTime을 가지고 있음
    private MeetingResponseDto.TimeRange makeTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        String startTime = startDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        String endTime = endDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));

        return MeetingResponseDto.TimeRange.builder()
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    // 해당 dateTimeInfo를 가지고 있는 attendee의 이름을 모두 불러오는 함수
    @Transactional
    public List<String> getUserNamesForDateTimeInfo(DateTimeInfo dateTimeInfo) {
        String query = "SELECT a.user.name FROM Attendee a WHERE a.dateTimeInfo = :dateTimeInfo";

        // Execute the query
        return entityManager.createQuery(query, String.class)
                .setParameter("dateTimeInfo", dateTimeInfo)
                .getResultList();
    }

    // LocalDateTime 형에서 time만 추출해서 String으로 변환하는 함수
    private String formatTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return dateTime.format(formatter);
    }

    public Double calculateRatioForDateTimeInfo(DateTimeInfo dateTimeInfo, Meeting meeting) {
        List<String> userNames = getUserNamesForDateTimeInfo(dateTimeInfo);
        Integer numberOfAttendee = userNames.size();
        Integer totalNumberOfUsers = meeting.getNumberOfVoter();

        if(numberOfAttendee == 0 || totalNumberOfUsers == 0)
            return 0.0;
        return (double) numberOfAttendee / totalNumberOfUsers;
    }

    public List<Map<String, List<MeetingResponseDto.DateTimeInfoDto>>> makeDateTimeInfoDto(Meeting meeting) {
        List<DateTimeInfo> dateTimeInfos = dateTimeInfoRepository.findByMeeting(meeting);
        List<Map<String, List<MeetingResponseDto.DateTimeInfoDto>>> resultList = new ArrayList<>();

        // 날짜별로 모든 시간대 정보를 하나의 맵에 넣기 위한 맵 생성
        Map<String, List<MeetingResponseDto.DateTimeInfoDto>> dateTimeInfoMap = new LinkedHashMap<>();

        for (DateTimeInfo dateTimeInfo : dateTimeInfos) {
            MeetingResponseDto.DateTimeInfoDto dateTimeInfoDto = MeetingResponseDto.DateTimeInfoDto.builder()
                    .time(formatTime(dateTimeInfo.getDatetime()))
                    .attendee(getUserNamesForDateTimeInfo(dateTimeInfo))
                    .ratio(calculateRatioForDateTimeInfo(dateTimeInfo, meeting))
                    .build();

            // 시간대 정보를 날짜별로 그룹화하여 맵에 추가
            String dateKey = dateTimeInfo.getDatetime().toLocalDate().toString();
            List<MeetingResponseDto.DateTimeInfoDto> dateTimeInfoDtoList = dateTimeInfoMap.getOrDefault(dateKey, new ArrayList<>());
            dateTimeInfoDtoList.add(dateTimeInfoDto);
            dateTimeInfoMap.put(dateKey, dateTimeInfoDtoList);
        }

        // 완성된 맵을 결과 리스트에 추가
        resultList.add(dateTimeInfoMap);

        return resultList;
    }


    private List<String> findConfirmedAttendee(Meeting meeting) {
        List<DateTimeInfo> dateTimeInfos = dateTimeInfoRepository.findByMeetingAndIsConfirmed(meeting, true);
        if (!dateTimeInfos.isEmpty()) {
            DateTimeInfo firstDateTimeInfo = dateTimeInfos.get(0);
            return getUserNamesForDateTimeInfo(firstDateTimeInfo);
        } else
            throw new CustomExceptions.Exception("아직 모임이 확정되지 않았습니다.");
    }
    private List<Map<String, List<MeetingResponseDto.DateInfoDto>>> makeDateInfoDto(Meeting meeting) {
        List<DateTimeInfo> dateTimeInfos = dateTimeInfoRepository.findByMeeting(meeting);

        List<Map<String, List<MeetingResponseDto.DateInfoDto>>> resultList = new ArrayList<>();

        for (DateTimeInfo dateTimeInfo : dateTimeInfos) {
            String formattedDate = dateTimeInfo.getDatetime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            MeetingResponseDto.DateInfoDto dateInfoDto = MeetingResponseDto.DateInfoDto.builder()
                    .attendee(getUserNamesForDateTimeInfo(dateTimeInfo))
                    .ratio(calculateRatioForDateTimeInfo(dateTimeInfo, meeting))
                    .build();

            Map<String, List<MeetingResponseDto.DateInfoDto>> dateInfoDtoMap = new HashMap<>();
            List<MeetingResponseDto.DateInfoDto> dateInfoDtoList = new ArrayList<>();
            dateInfoDtoList.add(dateInfoDto);
            dateInfoDtoMap.put(formattedDate, dateInfoDtoList);

            resultList.add(dateInfoDtoMap);
        }

        return resultList;
    }

    public Object getMeetingDetails(Meeting meeting) {
        if (meeting.getIsConfirmed() && !meeting.getOnlyDate())
            return getConfirmedDateTimeDetails(meeting);
        else if (meeting.getIsConfirmed() && meeting.getOnlyDate())
            return getConfirmedDateDetails(meeting);
        else if (!meeting.getIsConfirmed() && meeting.getOnlyDate())
            return getInProgressDateDetails(meeting);
        else if (!meeting.getIsConfirmed() && !meeting.getOnlyDate())
            return getInProgressDateTimeDetails(meeting);
        else
            throw new CustomExceptions.MeetingNotFoundException("모임이 없습니다.");
    }
    public MeetingResponseDto.MeetingConfirmedDateTimeDetailResponse getConfirmedDateTimeDetails(Meeting meeting) {

        MeetingResponseDto.TimeRange timeRange
                = makeTimeRange(meeting.getConfirmedStartDateTime(), meeting.getConfirmedEndDateTime());

        String creatorName = ""; // 먼저 변수를 초기화합니다.
        if (meeting.getCreator() != null) {
            creatorName = meeting.getCreator().getName();
        }

        return MeetingResponseDto.MeetingConfirmedDateTimeDetailResponse.builder()
                .id(meeting.getId())
                .createdAt(meeting.getCreatedAt())
                .numberOfSubmit(meeting.getNumberOfVoter())
                .confirmedDate(getDateStringFromLocalDateTime(meeting.getConfirmedStartDateTime()))
                .confirmedTime(timeRange)
                .confirmedAttendee(findConfirmedAttendee(meeting))
                .data(makeDateTimeInfoDto(meeting))
                .StatusCode(200)
                .ResponseMessage(ResponseMessage.GET_MEETING_DETAIL_SUCCESS)
                .creatorName(creatorName)
                .build();
    }

    private MeetingResponseDto.MeetingInProgressDateTimeDetailResponse getInProgressDateTimeDetails(Meeting meeting) {
        String creatorName = ""; // 먼저 변수를 초기화합니다.
        if (meeting.getCreator() != null) {
            creatorName = meeting.getCreator().getName();
        }
        return MeetingResponseDto.MeetingInProgressDateTimeDetailResponse.builder()
                .id(meeting.getId())
                .createdAt(meeting.getCreatedAt())
                .numberOfSubmit(meeting.getNumberOfVoter())
                .data(makeDateTimeInfoDto(meeting))
                .StatusCode(200)
                .ResponseMessage(ResponseMessage.GET_MEETING_DETAIL_SUCCESS)
                .creatorName(creatorName)
                .build();
    }

    private MeetingResponseDto.MeetingInProgressDateDetailResponse getInProgressDateDetails(Meeting meeting) {
        String creatorName = ""; // 먼저 변수를 초기화합니다.
        if (meeting.getCreator() != null) {
            creatorName = meeting.getCreator().getName();
        }
        return MeetingResponseDto.MeetingInProgressDateDetailResponse.builder()
                .id(meeting.getId())
                .createdAt(meeting.getCreatedAt())
                .numberOfSubmit(meeting.getNumberOfVoter())
                .data(makeDateInfoDto(meeting))
                .StatusCode(200)
                .ResponseMessage(ResponseMessage.GET_MEETING_DETAIL_SUCCESS)
                .creatorName(creatorName)
                .build();
    }



    private MeetingResponseDto.MeetingConfirmedDateDetailResponse getConfirmedDateDetails(Meeting meeting) {
        String creatorName = ""; // 먼저 변수를 초기화합니다.
        if (meeting.getCreator() != null) {
            creatorName = meeting.getCreator().getName();
        }
        return MeetingResponseDto.MeetingConfirmedDateDetailResponse.builder()
                .id(meeting.getId())
                .createdAt(meeting.getCreatedAt())
                .numberOfSubmit(meeting.getNumberOfVoter())
                .confirmedDate(getDateStringFromLocalDateTime(meeting.getConfirmedStartDateTime()))
                .confirmedAttendee(findConfirmedAttendee(meeting))
                .data(makeDateInfoDto(meeting))
                .StatusCode(200)
                .ResponseMessage(ResponseMessage.GET_MEETING_DETAIL_SUCCESS)
                .creatorName(creatorName)
                .build();
    }




// ------------------get meeting details 끝 ------------------------
}