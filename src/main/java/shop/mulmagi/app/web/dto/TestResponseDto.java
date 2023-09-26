package shop.mulmagi.app.web.dto;

import lombok.*;

import java.time.LocalDateTime;

public class TestResponseDto {

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateTestDto{
        private Long testId;
        private String name;
        private LocalDateTime createdAt;
    }
}
