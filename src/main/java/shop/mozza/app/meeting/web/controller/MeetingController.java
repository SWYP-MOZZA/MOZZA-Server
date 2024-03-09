package shop.mozza.app.meeting.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.mozza.app.base.BaseController;
import shop.mozza.app.exception.ResponseMessage;
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

    @PostMapping("/meeting/create")
    public ResponseEntity<?> createMeeting(@RequestBody MeetingRequestDto.makeMeetingRequest meetingRequest) {
        try {
            meetingService.createMeeting(meetingRequest);
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 200);
            response.put("message", ResponseMessage.MAKE_MEETING_SUCCESS);
            response.put("accessToken", "bearer+"); // user 완성되면 수정
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
            meetingService.addGuest(guestRequest);
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 200);
            response.put("message", ResponseMessage.GUEST_LOGIN_SUCCESS);
            response.put("accessToken", "bearer+"); // user 완성되면 수정
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
                return ResponseEntity.ok(new MeetingResponseDto.ResponseDto(200, ResponseMessage.SET_NOTIFICATION_ON_SUCCESS));
            } else {
                return ResponseEntity.ok(new MeetingResponseDto.ResponseDto(200, ResponseMessage.SET_NOTIFICATION_OFF_SUCCESS));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MeetingResponseDto.ResponseDto(400, ResponseMessage.SET_NOTIFICATION_FAILED));
        }
    }



    //모임 요약 정보
    @GetMapping("/meeting/{meetingID}/short")
    public ResponseEntity<?> getShortMeetingInfo(@PathVariable Long id){
        Meeting meeting = meetingService.findMeetingById(id);
        if (meeting == null) {
            return ResponseEntity.notFound().build();
        }
        MeetingResponseDto.SummaryResponse response = meetingService.createSummaryResponse(meeting);
        return ResponseEntity.ok(response);
    }


}
