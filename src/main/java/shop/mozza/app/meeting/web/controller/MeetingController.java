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
    @PostMapping("/meeting/create")
    public ResponseEntity<?> createMeeting(@RequestBody MeetingRequestDto.makeMeetingRequest meetingRequest) {
        try {
            meetingService.createMeeting(meetingRequest);
            Map<String, Object> response = new HashMap<>();

            // To DO - token 생성

            String accesToeken = jwtTokenPublisher.IssueMeetingToken();


            response.put("statusCode", 200);
            response.put("message", ResponseMessage.MAKE_MEETING_SUCCESS);
            response.put("accessToken", "bearer+"+accesToeken); // user 완성되면 수정
            response.put("url", "localhost:8080/");  // 일정 등록 url 완성되면 수정
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", 400);
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

            response.put("statusCode", 200);
            response.put("message", ResponseMessage.GUEST_LOGIN_SUCCESS);
            response.put("accessToken", "bearer+"+accessToken); // user 완성되면 수정
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", 400);
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
                return ResponseEntity.ok(new MeetingResponseDto.notificationResponse(200, ResponseMessage.SET_NOTIFICATION_ON_SUCCESS));
            } else {
                return ResponseEntity.ok(new MeetingResponseDto.notificationResponse(200, ResponseMessage.SET_NOTIFICATION_OFF_SUCCESS));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MeetingResponseDto.notificationResponse(400, ResponseMessage.SET_NOTIFICATION_FAILED));
        }
    }

}
