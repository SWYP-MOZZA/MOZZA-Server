package shop.mozza.app.meeting.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import shop.mozza.app.base.BaseController;
import shop.mozza.app.exception.ResponseMessage;
import shop.mozza.app.meeting.service.MeetingService;
import shop.mozza.app.meeting.web.dto.MeetingRequestDto;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MeetingController extends BaseController {
    @Autowired
    private MeetingService meetingService;

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
    public ResponseEntity<?> addGuest(@RequestBody MeetingRequestDto.guestRequest guestRequest){
        try{
            meetingService.addGuest(guestRequest);
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 200);
            response.put("message", ResponseMessage.GUEST_LOGIN_SUCCESS);
            response.put("accessToken", "bearer+"); // user 완성되면 수정
            return ResponseEntity.ok(response);

        }catch (Exception e){
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("statusCode", 400);
            errorResponse.put("ResponseMessage", ResponseMessage.GUEST_LOGIN_FAILED);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
