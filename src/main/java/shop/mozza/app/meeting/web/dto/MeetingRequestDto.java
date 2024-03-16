package shop.mozza.app.meeting.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


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
    public static class confirmDateTimeRequest{
        private String confirmedDate;
        private String confirmedStartTime;
        private String confirmedEndTime;
    }

    @Getter
    @Setter
    public static class confirmDateRequest{
        private String confirmedStartDate;
        private String confirmedEndDate;
    }


}
