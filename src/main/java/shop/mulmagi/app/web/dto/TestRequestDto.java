package shop.mulmagi.app.web.dto;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Getter;

public class TestRequestDto {

    @Getter
    public static class CreateTestDto{
        @ApiModelProperty(example = "test")
        @ApiParam(name = "name", value = "문자 입력", required = true)
        private String name;
    }
}
