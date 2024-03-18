package shop.mozza.app.meeting.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
        @Schema(name="name", example = "모임1")
        private String name;
        @Schema(name = "date", type = "array", example = "[\"2024-03-12\",  \"2024-03-13\"]")
        private List<String> date;
        @Schema(name = "startTime", example = "13:00")
        private String startTime;
        @Schema(name = "endTime", example = "14:00")
        private String endTime;
        @Schema(name = "onlyDate", example = "false")
        private Boolean onlyDate;

    }

    @Getter
    @Setter
    public static class guestRequest {
        @Schema(name = "name", example = "최유정")
        private String name;
        @Schema(name = "password", example = "1234")
        private String password;
    }

    @Getter
    @Setter
    public static class notificationRequest{
        @Schema(name = "ableNotification", example = "true")
        private Boolean ableNotification;
        @Schema(name = "numberOfVoter", example = "6")
        private Integer numberOfVoter;
    }

    @Getter
    @Setter
    public static class DateSubmitRequest{
        @Schema(name = "date", example = "2024-03-12")
        private String date;
        @Schema(name = "isActive", example = "true")
        private Boolean isActive;
    }

    @Getter
    @Setter
    public static class TimeSlot {
        @Schema(name = "time", example = "13:00")
        private String time;
        @Schema(name = "time", example = "isActive")
        private Boolean isActive;
    }

    @Getter
    @Setter
    public static class confirmDateTimeRequest{
        @Schema(name = "confirmedDate", example = "2024-03-12")
        private String confirmedDate;
        @Schema(name = "confirmedStartTime", example = "13:00")
        private String confirmedStartTime;
        @Schema(name = "confirmedEndTime", example = "14:00")
        private String confirmedEndTime;
    }

    @Getter
    @Setter
    public static class confirmDateRequest{
        @Schema(name = "confirmedStartDate", example = "2024-03-12")
        private String confirmedStartDate;
        @Schema(name = "confirmedEndDate", example = "2024-03-12")
        private String confirmedEndDate;
    }


}
