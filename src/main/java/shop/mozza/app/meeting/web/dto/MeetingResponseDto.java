package shop.mozza.app.meeting.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

public class MeetingResponseDto {
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class makeMeetingResponse{

        private String accessToken;
        private String url;

    }
}
