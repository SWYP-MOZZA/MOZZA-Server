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

    @Getter
    @Setter
    public static class notificationRequest{
        private Boolean ableNotification;
        private Integer numberOfVoter;
    }

    @Getter
    @Setter
    public static class DateSubmitRequest{
        private String date;
        private Boolean isActive;
    }

    @Getter
    @Setter
    public static class TimeSlot {
        private String time;
        private Boolean isActive;
    }

    @Getter
    @Setter
    public static class confirmRequest{
        private String confirmedDate;
        private String confirmedStartTime;
        private String confirmedEndTime;
    }
}
