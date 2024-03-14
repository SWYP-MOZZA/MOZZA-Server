package shop.mozza.app.meeting.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.mozza.app.base.BaseController;
import shop.mozza.app.exception.ResponseMessage;
import shop.mozza.app.login.jwt.token.JWTTokenPublisher;
import shop.mozza.app.login.user.domain.User;
import shop.mozza.app.login.user.service.UserService;
import shop.mozza.app.meeting.domain.Meeting;
import shop.mozza.app.meeting.service.MeetingService;
import shop.mozza.app.meeting.web.dto.MeetingRequestDto;
import shop.mozza.app.meeting.web.dto.MeetingResponseDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Meeting", description = "Meeting API")
public class MeetingController extends BaseController {

    private final MeetingService meetingService;

    private final JWTTokenPublisher jwtTokenPublisher;
    private final UserService userService;

    @PostMapping("/meeting/create")
    public ResponseEntity<?> createMeeting(@RequestBody MeetingRequestDto.makeMeetingRequest meetingRequest) {
        try {
            User user = userService.getCurrentUser();
            Long meetingId = meetingService.createMeeting(meetingRequest, user);
            String accessToken = jwtTokenPublisher.IssueMeetingToken();
            Map<String, Object> response = new HashMap<>();
            response.put("StatusCode", 200);
            response.put("ResponseMessage", ResponseMessage.MAKE_MEETING_SUCCESS);
            response.put("MeetingId",meetingId);
            response.put("AccessToken", accessToken);
            response.put("URL", "localhost:8080/");  // 일정 등록 url 완성되면 수정
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MeetingResponseDto.ErrorResponseDto(400, ResponseMessage.MAKE_MEETING_FAILED, e.getMessage() ));
        }
    }

    @PostMapping("/guest")
    public ResponseEntity<?> addGuest(@RequestBody MeetingRequestDto.guestRequest guestRequest) {
        try {
            User user = meetingService.addGuest(guestRequest);
            Map<String, Object> response = new HashMap<>();
            // To DO Jwt 토큰 발급
            String accessToken = jwtTokenPublisher.IssueGuestToken(user);

            response.put("StatusCode", 200);
            response.put("ResponseMessage", ResponseMessage.GUEST_LOGIN_SUCCESS);
            response.put("AccessToken", accessToken);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MeetingResponseDto.ErrorResponseDto(400, ResponseMessage.GUEST_LOGIN_FAILED, e.getMessage()));
        }
    }

    @PutMapping("/meeting/{id}/notification")
    public ResponseEntity<?> setNotification(@PathVariable Long id, @RequestBody MeetingRequestDto.notificationRequest notificationRequest) {
        try {
            Long meetingId = id;
            meetingService.setNotification(notificationRequest, meetingId);
            if (notificationRequest.getAbleNotification()) {
                return ResponseEntity.ok(new MeetingResponseDto.ResponseDto(200, ResponseMessage.SET_NOTIFICATION_ON_SUCCESS));
            } else {
                return ResponseEntity.ok(new MeetingResponseDto.ResponseDto(200, ResponseMessage.SET_NOTIFICATION_OFF_SUCCESS));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MeetingResponseDto.ErrorResponseDto(400, ResponseMessage.SET_NOTIFICATION_FAILED, e.getMessage()));
        }
    }


    //모임 요약 정보
    @GetMapping("/meeting/{id}/short")
    public ResponseEntity<?> getShortMeetingInfo(@PathVariable Long id) {
        try {
            Meeting meeting = meetingService.findMeetingById(id);
            if (meeting == null) {
                return ResponseEntity.badRequest().body(new MeetingResponseDto.ResponseDto(404, ResponseMessage.GET_MEEITNG_FAILED));
            }
            MeetingResponseDto.SummaryResponse summaryResponse = meetingService.createSummaryResponse(meeting);
            Map<String, Object> response = new HashMap<>();
            response.put("StatusCode", 200);
            response.put("ResponseMessage", ResponseMessage.GET_MEEITNG_INFO_SUCCESS);
            response.put("Data", summaryResponse);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MeetingResponseDto.ErrorResponseDto(400, ResponseMessage.GET_MEEITNG_INFO_FAILED, e.getMessage()));
        }
    }

    //모임 투표를 위한 정보
    @GetMapping("meeting/{id}/choice")
    public ResponseEntity<?> getMeetingOptions(@PathVariable Long id) {
        try {
            Meeting meeting = meetingService.findMeetingById(id);
            if (meeting == null) {
                return ResponseEntity.badRequest().body(new MeetingResponseDto.ResponseDto(404, ResponseMessage.GET_MEEITNG_FAILED));
            }
            MeetingResponseDto.ChoiceResponse choiceResponse = meetingService.createChoiceResponse(meeting);
            Map<String, Object> response = new HashMap<>();
            response.put("StatusCode", 200);
            response.put("ResponseMessage", ResponseMessage.GET_MEEITNG_INFO_SUCCESS);
            response.put("Data", choiceResponse);
            return ResponseEntity.ok(response);


        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MeetingResponseDto.ErrorResponseDto(400, ResponseMessage.GET_MEEITNG_INFO_FAILED, e.getMessage()));
        }
    }

    @PostMapping("/meeting/{id}/date/submit")
    public ResponseEntity<?> submitMeetingDate(@PathVariable Long id, @RequestBody List<MeetingRequestDto.DateSubmitRequest> dateRequests) {
        try {
            User user = userService.getCurrentUser();
            meetingService.submitMeetingDate(user,id, dateRequests);
            return ResponseEntity.ok(new MeetingResponseDto.ResponseDto(200, ResponseMessage.SUBMIT_SCHEDULE_SUCCESS));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(new MeetingResponseDto.ResponseDto(400, ResponseMessage.SUBMIT_SCHEDULE_FAILED));
        }
    }

    @PostMapping("/meeting/{id}/submit")
    public ResponseEntity<?> submitMeetingDateTime(@PathVariable Long id, @RequestBody Map<String, List<MeetingRequestDto.TimeSlot>> dateTimeRequests) {
        try {
            User user = userService.getCurrentUser();
            meetingService.submitMeetingDateTime(user,id, dateTimeRequests);
            return ResponseEntity.ok(new MeetingResponseDto.ResponseDto(200, ResponseMessage.SUBMIT_SCHEDULE_SUCCESS));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(new MeetingResponseDto.ErrorResponseDto(400, ResponseMessage.SUBMIT_SCHEDULE_FAILED, e.getMessage()));
        }
    }
@GetMapping("/all-meetings")
public ResponseEntity<?> GetAllMeetings() {
    try {
        User user = userService.getCurrentUser();
        List<Meeting> meetings = meetingService.findMeetingsByUser(user);
        if (meetings == null || meetings.isEmpty()) {
            return ResponseEntity.badRequest().body(new MeetingResponseDto.ResponseDto(404, ResponseMessage.NO_MEEITNG_LIST_ERROR));
        }

        MeetingResponseDto.AllMeetingResponseDto responseDto = meetingService.findAllmeetings(user);

        return ResponseEntity.ok(responseDto);
    }
    catch (Exception e){
        return ResponseEntity.badRequest().body(new MeetingResponseDto.ErrorResponseDto(400, ResponseMessage.GET_ALL_MEETING_FAILED, e.getMessage()));
    }
}






}
