package shop.mozza.app.web.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class TestRequestDto {

    @Getter
    public static class CreateTestDto{
        @Schema(example = "test")
        @Parameter(name = "name", required = true)
        private String name;
    }
}
