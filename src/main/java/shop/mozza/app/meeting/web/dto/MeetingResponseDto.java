package shop.mozza.app.meeting.web.dto;

import lombok.*;

import java.util.List;

public class MeetingResponseDto {
    @Setter
    @Getter
    public static class notificationResponse{
        private Integer StatusCode;
        private String responseMessage;

        public notificationResponse(Integer statusCode, String responseMessage) {
            this.responseMessage = responseMessage;
            this.StatusCode = statusCode;
        }
    }


    @Getter
    @Setter
    public static class summaryResponse{
        private Long meetingId;
        private String name;
        private String startDate;
        private String endDate;
        private String startTime;
        private String endTime;
        private Integer numberOfVoter;
        private List<String> attendee;
    }


}
