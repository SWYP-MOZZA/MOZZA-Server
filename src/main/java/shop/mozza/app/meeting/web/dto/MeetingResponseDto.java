package shop.mozza.app.meeting.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;
import shop.mozza.app.exception.ResponseMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class MeetingResponseDto {
    @Setter
    @Getter
    @AllArgsConstructor
    public static class ResponseDto{
        @Schema(name = "statusCode", example = "200")
        private Integer StatusCode;
        @Schema(name = "ResponseMessage" , example = "요청에 성공했습니다.")
        private String ResponseMessage;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ErrorResponseDto{
        @Schema (name = "statusCode", example = "400")
        private Integer StatusCode;
        @Schema(name = "responseMessage", example = "요청 처리를 실패했습니다.")
        private String ResponseMessage;
        private String ErrorMessage;
    }

    @Getter
    @Setter
    @Builder
    public static class CreateResponse{
        @Schema(name = "statusCode", example = "200")
        private Integer statusCode;
        @Schema(name = "responseMessage", example = shop.mozza.app.exception.ResponseMessage.MAKE_MEETING_SUCCESS)
        private String ResponseMessage;

        @Schema(name = "meetingId" , example = "1")
        private Long meetingId;
        @Schema(name = "accessToken", example = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJpYXQiOjE3MTA2MTg0OTYsImV4cCI6MTcxMDYxOTA5Nn0.vqzTLeVNN9YxUK2lS0YFhDu1PMqYzQ1mn5dx_BwRVFrnlTz8T4jPveNw0vad-3FA6C2XQIf3qvsgTCSHL_j2IQ")
        private String accessToken;

        @Schema(name = "URL", example = "mozza.com/meeting/1/short")
        private String URL;

    }


    @Getter
    @Setter
    @Builder
    public static class SummaryResponse{
        @Schema(name = "meetingId", example = "1")
        private Long meetingId;
        @Schema(name = "name", example = "모임1")
        private String name;
        @Schema(name = "startDate", example = "2024-03-12")
        private String startDate;
        @Schema(name = "endDate", example = "2024-03-13")
        private String endDate;
        @Schema(name = "startTime", example = "13:00")
        private String startTime;
        @Schema(name = "endTime", example = "14:00")
        private String endTime;
        @Schema(name = "numberOfVoter", example = "2")
        private Integer numberOfVoter;
        @Schema(name = "attendee", type = "array", example = "[\"최유정\",  \"여성찬\"]")
        private List<String> attendee;
    }

    @Getter
    @Setter
    @Builder
    public static class ChoiceResponse{
        @Schema(name = "meetingId", example = "1")
        private Long meetingId;
        @Schema(name = "name", example = "모임1")
        private String name;
        @Schema(name = "date", type = "array", example = "[\"2024-03-12\",  \"2024-03-13\"]")
        private List<String> date;
        @Schema(name = "startTime", example = "13:00")
        private String startTime;
        @Schema(name = "endTime", example = "14:00")
        private String endTime;
    }
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AllMeetingResponseDto {
        @Schema(name = "statusCode", example = "200")
        private Integer StatusCode;
        private List<MeetingInfo> ConfirmedMeetings;
        private List<MeetingInfo> InProgress;
        @Schema(name = "responseMessage", example = shop.mozza.app.exception.ResponseMessage.GET_ALL_MEETING_SUCCESS)
        private String ResponseMessage;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MeetingInfo {
        @Schema(name = "meetingId", example = "1")
        private Long meetingId;
        @Schema(name = "meetingName", example = "모임1")
        private String meetingName;
        @Schema(name = "confirmedDate", example = "2024-03-12")
        private String confirmedDate;
        // @Schema(name= "confirmedTime", type = TimeInfo, example = )
        private TimeInfo confirmedTime;
        @Schema(name = "submitUserNumber", example = "2")
        private Integer submitUserNumber;
        @Schema(name = "createdAt", example = "2024-03-16T11:30:49")
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TimeInfo {
        @Schema(name = "startTime", example = "13:00")
        private String startTime;
        @Schema(name = "endTime", example = "13:30")
        private String endTime;
    }


    @Builder
    @Getter
    @Setter
    public static class confirmResponse{
        @Schema(name = "id", example = "1")
        private Long id;
        @Schema(name = "createdAt", example = "2024-03-16T11:30:49")
        private LocalDateTime createdAt;
        @Schema(name = "numberOfSubmit", example = "2")
        private Integer numberOfSubmit;
        @Schema(name = "confirmedDate", example = "2024-03-12")
        private String confirmedDate;
        @Schema(name = "confirmedStartTime", example = "13:00")
        private String confirmedStartTime;
        @Schema(name = "confirmedEndTime", example = "14:00")
        private String confirmedEndTime;
        @Schema(name = "StatusCode", example = "200")
        private Integer StatusCode;
        @Schema(name = "ResponseMessage" , example = shop.mozza.app.exception.ResponseMessage.CONFIRM_MEETING_SUCCESS)
        private String ResponseMessage;
    }

    @Builder
    @Getter
    @Setter
    public static class confirmDateResponse{
        @Schema(name = "id", example = "1")
        private Long id;
        @Schema(name = "createdAt", example = "2024-03-16T11:30:49")
        private LocalDateTime createdAt;
        @Schema(name = "numberOfSubmit", example = "2")
        private Integer numberOfSubmit;
        @Schema(name = "confirmedStartDate", example = "2024-03-12")
        private String confirmedStartDate;
        @Schema(name = "confirmedEndDate", example = "2024-03-12")
        private String confirmedEndDate;
        @Schema(name = "statusCode", example = "200")
        private Integer StatusCode;
        @Schema(name = "ResponseMessage" , example = shop.mozza.app.exception.ResponseMessage.CONFIRM_MEETING_SUCCESS)
        private String ResponseMessage;
    }

    @Getter
    @Setter
    @Builder
    public static class TimeRange {
        @Schema(name = "startTime", example = "13:00")
        private String startTime;
        @Schema(name = "endTime", example = "13:30")
        private String endTime;

    }

    @Getter
    @Setter
    @Builder
    public static class DateTimeInfoDto {
        @Schema(name = "time", example = "13:00")
        private String time;
        @Schema(name = "attendee", type = "array", example = "[\"최유정\",  \"여성찬\"]")
        private List<String> attendee;
        @Schema(name = "ratio", example = "1")
        private Double ratio;

    }

    @Getter
    @Setter
    @Builder
    public static class DateInfoDto {
        @Schema(name = "attendee", type = "array", example = "[\"최유정\",  \"여성찬\"]")
        private List<String> attendee;
        @Schema(name = "ratio", example = "1")
        private Double ratio;

    }

    @Getter
    @Setter
    @Builder
    public static class MeetingConfirmedDateTimeDetailResponse {
        @Schema(name = "id", example = "1")
        private Long id;
        @Schema(name = "createdAt", example = "2024-03-16T11:30:49")
        private LocalDateTime createdAt;
        @Schema(name = "numberOfSubmit", example = "2")
        private Integer numberOfSubmit;
        private String confirmedDate;
        private TimeRange confirmedTime;
        private List<String> confirmedAttendee;
        private Map<String, List<DateTimeInfoDto>> data;
        @Schema(name = "StatusCode", example = "200")
        private Integer StatusCode;
        @Schema(name = "ResponseMessage" , example = shop.mozza.app.exception.ResponseMessage.GET_MEETING_DETAIL_SUCCESS)
        private String ResponseMessage;


    }
    @Getter
    @Setter
    @Builder
    public static class MeetingInProgressDateTimeDetailResponse {
        @Schema(name = "id", example = "1")
        private Long id;
        @Schema(name = "createdAt", example = "2024-03-16T11:30:49")
        private LocalDateTime createdAt;
        @Schema(name = "numberOfSubmit", example = "2")
        private Integer numberOfSubmit;
        private Map<String, List<DateTimeInfoDto>> data;
        @Schema(name = "StatusCode", example = "200")
        private Integer StatusCode;
        @Schema(name = "ResponseMessage" , example = shop.mozza.app.exception.ResponseMessage.GET_MEETING_DETAIL_SUCCESS)
        private String ResponseMessage;

    }

    @Getter
    @Setter
    @Builder
    public static class MeetingConfirmedDateDetailResponse {
        @Schema(name = "id", example = "1")
        private Long id;
        @Schema(name = "createdAt", example = "2024-03-16T11:30:49")
        private LocalDateTime createdAt;
        @Schema(name = "numberOfSubmit", example = "2")
        private Integer numberOfSubmit;
        @Schema(name = "confirmedDate", example = "2024-03-12")
        private String confirmedDate;
        @Schema(name = "attendee", type = "array", example = "[\"최유정\",  \"여성찬\"]")
        private List<String> confirmedAttendee;
        private Map<String, List<DateInfoDto>> data;
        @Schema(name = "StatusCode", example = "200")
        private Integer StatusCode;
        @Schema(name = "ResponseMessage" , example = shop.mozza.app.exception.ResponseMessage.GET_MEETING_DETAIL_SUCCESS)
        private String ResponseMessage;


    }

    @Getter
    @Setter
    @Builder
    public static class MeetingInProgressDateDetailResponse {
        @Schema(name = "id", example = "1")
        private Long id;
        @Schema(name = "createdAt", example = "2024-03-16T11:30:49")
        private LocalDateTime createdAt;
        @Schema(name = "numberOfSubmit", example = "2")
        private Integer numberOfSubmit;
        private Map<String, List<DateInfoDto>> data;
        @Schema(name = "StatusCode", example = "200")
        private Integer StatusCode;
        @Schema(name = "ResponseMessage" , example = shop.mozza.app.exception.ResponseMessage.GET_MEETING_DETAIL_SUCCESS)
        private String ResponseMessage;

    }

}
