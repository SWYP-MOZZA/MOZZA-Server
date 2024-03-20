package shop.mozza.app.meeting.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
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
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Meeting", description = "Meeting API")
public class MeetingController extends BaseController {

    private final MeetingService meetingService;

    private final JWTTokenPublisher jwtTokenPublisher;
    private final UserService userService;


    @Operation(summary = "모임 생성 API",
            description = "모임의 후보 시간대와 날짜의 후보군을 설정해 모임을 생성한다. ",
            tags = {"meeting", "post"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = MeetingResponseDto.CreateResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {@Content(schema = @Schema(implementation = MeetingRequestDto.makeMeetingRequest.class, contentMediaType = "application/json"))})
    @PostMapping("/meeting/create")
    public ResponseEntity<?> createMeeting(@RequestBody MeetingRequestDto.makeMeetingRequest meetingRequest, HttpSession session) {
        try {
            MeetingResponseDto.CreateResponse response = meetingService.createMeeting(meetingRequest);
            session.setAttribute("meetingId", response.getMeetingId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MeetingResponseDto.ErrorResponseDto(400, ResponseMessage.MAKE_MEETING_FAILED, e.getMessage()));
        }
    }

    @Operation(summary = "비회원 등록 API",
            description = "비회원은 이름은 필수이며 비밀번호는 선택적으로 입력을 해서 토큰을 발급받는다.",
            tags = {"meeting", "post"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = MeetingResponseDto.ResponseDto.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})})
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

    @Operation(summary = "알림 설정 API",
            description = "원하는 인원만큼 일정을 등록했을 때 알림을 보내주기 위해 알림 수신 여부와 원하는 인원을 설정한다.",
            tags = {"meeting", "put"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = MeetingResponseDto.ResponseDto.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})})
    @PutMapping("/meeting/{id}/notification")
    public ResponseEntity<?> setNotification(@PathVariable("id") @Parameter(name = "id", example = "1")Long id, @RequestBody MeetingRequestDto.notificationRequest notificationRequest) {
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


    @Operation(summary = "모임 요약 정보 API",
            description = "링크를 타고 들어갔을때 처음 보이는 페이지의 정보를 보여준다.",
            tags = {"meeting", "get"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = MeetingResponseDto.SummaryResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})})
    @GetMapping("/meeting/{id}/short")
    public ResponseEntity<?> getShortMeetingInfo(@PathVariable("id") @Parameter(name = "id", example = "1")Long id) {
        try {
            Meeting meeting = meetingService.findMeetingById(id);
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

    @Operation(summary = "일정 등록을 위한 정보 API",
            description = "일정 등록을 위해서 meeting의 가능한 날짜와 시간대를 보여준다.",
            tags = {"meeting", "get"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = MeetingResponseDto.ChoiceResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})})
    @GetMapping("meeting/{id}/choice")
    public ResponseEntity<?> getMeetingOptions(@PathVariable("id") @Parameter(name = "id", example = "1")Long id) {
        try {
            Meeting meeting = meetingService.findMeetingById(id);
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

    @Operation(summary = "날짜만 선택하는 API",
            description = "가능한 일정의 날짜를 선택한다.",
            tags = {"meeting", "post"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = MeetingResponseDto.ResponseDto.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})})
    @PostMapping("/meeting/{id}/date/submit")
    public ResponseEntity<?> submitMeetingDate(@PathVariable("id") @Parameter(name = "id", example = "1")Long id, @RequestBody List<MeetingRequestDto.DateSubmitRequest> dateRequests) {
        try {
            User user = userService.getCurrentUser().orElseThrow();
            meetingService.submitMeetingDate(user, id, dateRequests);
            return ResponseEntity.ok(new MeetingResponseDto.ResponseDto(200, ResponseMessage.SUBMIT_SCHEDULE_SUCCESS));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MeetingResponseDto.ResponseDto(400, ResponseMessage.SUBMIT_SCHEDULE_FAILED));
        }
    }

    @Operation(summary = "날짜와 시간을 모두 선택하는 API",
            description = "가능한 일정의 날짜와 시간을 선택한다.",
            tags = {"meeting", "post"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = MeetingResponseDto.ResponseDto.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})})
    @PostMapping("/meeting/{id}/submit")
    public ResponseEntity<?> submitMeetingDateTime(@PathVariable("id") @Parameter(name = "id", example = "1")Long id, @RequestBody Map<String, List<MeetingRequestDto.TimeSlot>> dateTimeRequests) {
        try {
            User user = userService.getCurrentUser().orElseThrow();
            meetingService.submitMeetingDateTime(user, id, dateTimeRequests);
            return ResponseEntity.ok(new MeetingResponseDto.ResponseDto(200, ResponseMessage.SUBMIT_SCHEDULE_SUCCESS));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MeetingResponseDto.ErrorResponseDto(400, ResponseMessage.SUBMIT_SCHEDULE_FAILED, e.getMessage()));
        }
    }

    @Operation(summary = "내 모임 모두 조회 API",
            description = "로그인한 유저의 삭제되지 않은 모든 meeting을 불러온다.",
            tags = {"meeting", "get"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = MeetingResponseDto.AllMeetingResponseDto.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})})
    @GetMapping("/all-meetings")
    public ResponseEntity<?> GetAllMeetings() {
        try {
            User user = userService.getCurrentUser().orElseThrow();
            List<Meeting> meetings = meetingService.findMeetingsByUser(user);
            if (meetings == null || meetings.isEmpty()) {
                return ResponseEntity.badRequest().body(new MeetingResponseDto.ResponseDto(404, ResponseMessage.NO_MEEITNG_LIST_ERROR));
            }

            MeetingResponseDto.AllMeetingResponseDto responseDto = meetingService.findAllmeetings(user);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MeetingResponseDto.ErrorResponseDto(400, ResponseMessage.GET_ALL_MEETING_FAILED, e.getMessage()));
        }
    }


    @Operation(summary = "시간과 날짜 확정 API",
            description = "원하는 날짜의 원하는 시간으로 모임 확정한다.",
            tags = {"meeting", "put"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = MeetingResponseDto.MeetingConfirmedDateTimeDetailResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})})
    @PutMapping("/meeting/{id}/confirm")
    public ResponseEntity<?> confirmMeetings(@PathVariable("id") @Parameter(name = "id", example = "1")Long id, @RequestBody MeetingRequestDto.confirmDateTimeRequest request) {
        try {
            User user = userService.getCurrentUser().orElseThrow();
            Meeting meeting = meetingService.findMeetingById(id);

            // 현재 유저가 모임장이 아닐 때 예외 추가
           if (!meeting.getCreator().equals(user))
                return ResponseEntity.badRequest().body(new MeetingResponseDto.ResponseDto(403, ResponseMessage.USER_NOT_CREATOR));

            return ResponseEntity.ok(meetingService.confirmMeeting(meeting, request));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MeetingResponseDto.ErrorResponseDto(400, ResponseMessage.CONFIRM_MEETING_FAILED, e.getMessage()));
        }
    }

    @Operation(summary = "원하는 날짜만 확정 API",
            description = "날짜만 조율하는 일정의 날짜만을 확정하는 API",
            tags = {"meeting", "put"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = MeetingResponseDto.MeetingConfirmedDateDetailResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})})
    @PutMapping("/meeting/{id}/date/confirm")
    public ResponseEntity<?> confirmMeetings(@PathVariable("id") @Parameter(name = "id", example = "1")Long id, @RequestBody MeetingRequestDto.confirmDateRequest request) {
        try {
            User user = userService.getCurrentUser().orElseThrow();
            Meeting meeting = meetingService.findMeetingById(id);

            // 현재 유저가 모임장이 아닐 때 예외 추가
            if (!meeting.getCreator().equals(user))
                return ResponseEntity.badRequest().body(new MeetingResponseDto.ResponseDto(403, ResponseMessage.USER_NOT_CREATOR));

            return ResponseEntity.ok(meetingService.confirmDateMeeting(meeting, request));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MeetingResponseDto.ErrorResponseDto(400, ResponseMessage.CONFIRM_MEETING_FAILED, e.getMessage()));
        }
    }

    @Operation(summary = "모임의 자세한 정보를 보여주는 API",
            description = "내 모임 조회에서 선택한 모임의 자세한 정보를 보여준다.",
            tags = {"meeting", "get"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = MeetingResponseDto.MeetingInProgressDateDetailResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = MeetingResponseDto.MeetingConfirmedDateDetailResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = MeetingResponseDto.MeetingInProgressDateTimeDetailResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = MeetingResponseDto.MeetingConfirmedDateTimeDetailResponse.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema())})})
    @GetMapping("/meeting/{id}/details")
    public ResponseEntity<?> getMeetingDetail(@PathVariable("id") @Parameter(name = "id", example = "1")Long id) {
        try {
            Meeting meeting = meetingService.findMeetingById(id);
            return ResponseEntity.ok(meetingService.getMeetingDetails(meeting));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MeetingResponseDto.ErrorResponseDto(400, ResponseMessage.GET_MEETING_DETAILS_FAILED, e.getMessage()));
        }
    }


}
