package shop.mozza.app.meeting.web.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;
import shop.mozza.app.meeting.domain.DateTimeInfo;
import shop.mozza.app.meeting.domain.Meeting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class MeetingResponseDto {
    @Setter
    @Getter
    public static class ResponseDto{
        private Integer StatusCode;
        private String responseMessage;

        public ResponseDto(Integer statusCode, String responseMessage) {
            this.responseMessage = responseMessage;
            this.StatusCode = statusCode;
        }
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

    @Getter
    @Setter
    @Builder
    public static class MeetingDetailsResponse {
        private Long meetingId;
        private LocalDateTime createdAt;
        private Integer numberOfSubmit;
        private LocalDate confirmedDate;
        private Meeting.ConfirmedTime confirmedTime;
        private List<String> confirmedAttendee;
        private List<MeetingDetailsData> data;
    }

    @Getter
    @Setter
    @Builder
    public static class  MeetingDetailsData{
        private LocalDate localDate;
        private List<DateTimeInfo> dateTimeInfos;
    }

}
