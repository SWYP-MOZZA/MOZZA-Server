package shop.mozza.app.meeting.web.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class MeetingResponseDto {
    @Setter
    @Getter
    @AllArgsConstructor
    public static class ResponseDto{
        private Integer StatusCode;
        private String ResponseMessage;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ErrorResponseDto{
        private Integer StatusCode;
        private String ResponseMessage;
        private String ErrorMessage;
    }


    @Getter
    @Setter
    @Builder
    public static class SummaryResponse{
        private Long meetingId;
        private String name;
        private String startDate;
        private String endDate;
        private String startTime;
        private String endTime;
        private Integer numberOfVoter;
        private List<String> attendee;
    }

    @Getter
    @Setter
    @Builder
    public static class ChoiceResponse{
        private Long meetingId;
        private String name;
        private List<String> date;
        private String startTime;
        private String endTime;
    }
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AllMeetingResponseDto {
        private Integer StatusCode;
        private List<MeetingInfo> ConfirmedMeetings;
        private List<MeetingInfo> InProgress;
        private String ResponseMessage;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MeetingInfo {
        private Long meetingId;
        private String meetingName;
        private String confirmedDate;
        private TimeInfo confirmedTime;
        private Integer submitUserNumber;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TimeInfo {
        private String startTime;
        private String endTime;
    }


    @Builder
    @Getter
    @Setter
    public static class confirmResponse{
        private Long id;
        private LocalDateTime createdAt;
        private Integer numberOfSubmit;
        private String confirmedDate;
        private String confirmedStartTime;
        private String confirmedEndTime;
        private Integer statusCode;
        private String responseMessage;
    }

    @Builder
    @Getter
    @Setter
    public static class confirmDateResponse{
        private Long id;
        private LocalDateTime createdAt;
        private Integer numberOfSubmit;
        private String confirmedStartDate;
        private String confirmedEndDate;
        private Integer statusCode;
        private String responseMessage;
    }

    @Getter
    @Setter
    @Builder
    public static class TimeRange {
        private String startTime;
        private String endTime;

    }

    @Getter
    @Setter
    @Builder
    public static class DateTimeInfoDto {
        private String time;
        private List<String> attendee;
        private Double ratio;

    }

    @Getter
    @Setter
    @Builder
    public static class DateInfoDto {
        private List<String> attendee;
        private Double ratio;

    }

    @Getter
    @Setter
    @Builder
    public static class MeetingConfirmedDateTimeDetailResponse {
        private Long id;
        private LocalDateTime createdAt;
        private Integer numberOfSubmit;
        private String confirmedDate;
        private TimeRange confirmedTime;
        private List<String> confirmedAttendee;
        private Map<String, List<DateTimeInfoDto>> data;
        private Integer statusCode;
        private String responseMessage;


    }
    @Getter
    @Setter
    @Builder
    public static class MeetingInProgressDateTimeDetailResponse {
        private Long id;
        private LocalDateTime createdAt;
        private Integer numberOfSubmit;
        private Map<String, List<DateTimeInfoDto>> data;
        private Integer statusCode;
        private String responseMessage;

    }

    @Getter
    @Setter
    @Builder
    public static class MeetingConfirmedDateDetailResponse {
        private Long id;
        private LocalDateTime createdAt;
        private Integer numberOfSubmit;
        private String confirmedDate;
        private List<String> confirmedAttendee;
        private Map<String, List<DateInfoDto>> data;
        private Integer statusCode;
        private String responseMessage;


    }

    @Getter
    @Setter
    @Builder
    public static class MeetingInProgressDateDetailResponse {
        private Long id;
        private LocalDateTime createdAt;
        private Integer numberOfSubmit;
        private Map<String, List<DateInfoDto>> data;
        private Integer statusCode;
        private String responseMessage;

    }

}
