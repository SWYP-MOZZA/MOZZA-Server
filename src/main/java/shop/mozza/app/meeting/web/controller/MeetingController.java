package shop.mozza.app.meeting.web.controller;

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
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
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
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("StatusCode", 400);
            errorResponse.put("ResponseMessage", ResponseMessage.MAKE_MEETING_FAILED);
            return ResponseEntity.badRequest().body(errorResponse);
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
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("StatusCode", 400);
            errorResponse.put("ResponseMessage", ResponseMessage.GUEST_LOGIN_FAILED);
            return ResponseEntity.badRequest().body(errorResponse);
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
            return ResponseEntity.badRequest().body(new MeetingResponseDto.ResponseDto(400, ResponseMessage.SET_NOTIFICATION_FAILED));
        }
    }


    //모임 요약 정보
    @GetMapping("/meeting/{id}/short")
    public ResponseEntity<?> getShortMeetingInfo(@PathVariable Long id) {
        try {
            Meeting meeting = meetingService.findMeetingById(id);
            if (meeting == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("StatusCode", 404);
                errorResponse.put("ResponseMessage", ResponseMessage.GET_MEEITNG_FAILED);
                return ResponseEntity.badRequest().body(errorResponse);
            }
            MeetingResponseDto.SummaryResponse summaryResponse = meetingService.createSummaryResponse(meeting);
            Map<String, Object> response = new HashMap<>();
            response.put("StatusCode", 200);
            response.put("ResponseMessage", ResponseMessage.GET_MEEITNG_INFO_SUCCESS);
            response.put("Data", summaryResponse);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("StatusCode", 400);
            errorResponse.put("ResponseMessage", ResponseMessage.GET_MEEITNG_INFO_FAILED);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    //모임 투표를 위한 정보
    @GetMapping("meeting/{id}/choice")
    public ResponseEntity<?> getMeetingOptions(@PathVariable Long id) {
        try {
            Meeting meeting = meetingService.findMeetingById(id);
            if (meeting == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("StatusCode", 404);
                errorResponse.put("ResponseMessage", ResponseMessage.GET_MEEITNG_FAILED);
                return ResponseEntity.badRequest().body(errorResponse);
            }
            MeetingResponseDto.ChoiceResponse choiceResponse = meetingService.createChoiceResponse(meeting);
            Map<String, Object> response = new HashMap<>();
            response.put("StatusCode", 200);
            response.put("ResponseMessage", ResponseMessage.GET_MEEITNG_INFO_SUCCESS);
            response.put("Data", choiceResponse);
            return ResponseEntity.ok(response);


        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("StatusCode", 400);
            errorResponse.put("ResponseMessage", ResponseMessage.GET_MEEITNG_INFO_FAILED);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


    @PostMapping("meeting/delete")
    public ResponseEntity<?> deleteMeeting(@RequestBody MeetingRequestDto.deleteRequest deleteRequest) {
        try {
            Boolean result = meetingService.deleteMeetingById(deleteRequest.getId());
            if (result) {
                Map<String, Object> response = new HashMap<>();
                response.put("StatusCode", 200);
                response.put("ResponseMessage", ResponseMessage.Delete_MEEITNG_SUCCESS);
                return ResponseEntity.ok(response);
            }
            else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("StatusCode", 400);
                errorResponse.put("ResponseMessage", ResponseMessage.Delete_MEEITNG_FAILED);
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("StatusCode", 400);
            errorResponse.put("ResponseMessage", ResponseMessage.Delete_MEEITNG_FAILED);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


    // 모임정보 - Details
    @GetMapping("/meeting/${meetingId}/details")
    public ResponseEntity<?> getMeetingDetails(@PathVariable Long meetingId) {
        try {
            Meeting meeting = meetingService.findMeetingById(meetingId);
            if (meeting == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("StatusCode", 404);
                errorResponse.put("ResponseMessage", ResponseMessage.GET_MEEITNG_DETAILS_FAILED);
                return ResponseEntity.badRequest().body(errorResponse);
            }
            MeetingResponseDto.MeetingDetailsResponse detailsResponse = meetingService.getMeetingDetails(meeting);
            Map<String, Object> response = new HashMap<>();
            response.put("StatusCode", 200);
            response.put("ResponseMessage", ResponseMessage.GET_MEEITNG_INFO_SUCCESS);
            response.put("Data", choiceResponse);
            return ResponseEntity.ok(response);


        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("StatusCode", 400);
            errorResponse.put("ResponseMessage", ResponseMessage.GET_MEEITNG_DETAILS_FAILED);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

}
