package shop.mozza.app.meeting.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


public class MeetingRequestDto {
    @Getter
    @Setter
    public static class makeMeetingRequest {
        private String name;
        private List<String> date;
        private String startTime;
        private String endTime;
        private Boolean onlyDate;

    }

    @Getter
    @Setter
    public static class guestRequest {
        private String name;
        private String password;
    }
}