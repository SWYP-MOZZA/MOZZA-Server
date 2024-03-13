package shop.mozza.app.meeting.web.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;

import java.util.List;

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




}
