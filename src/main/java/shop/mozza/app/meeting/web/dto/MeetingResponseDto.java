package shop.mozza.app.meeting.web.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;

import java.util.List;

public class MeetingResponseDto {
    @Setter
    @Getter
    public static class ResponseDto{
        private Integer StatusCode;
        private String ResponseMessage;

        public ResponseDto(Integer statusCode, String responseMessage) {
            this.ResponseMessage = responseMessage;
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




}
