package shop.mozza.app.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.mozza.app.domain.Test;
import shop.mozza.app.web.dto.TestResponseDto;

@Component
@RequiredArgsConstructor
public class TestConverter {

    public static TestResponseDto.CreateTestDto toCreateTestDto(Test test) {
        return TestResponseDto.CreateTestDto.builder()
                .testId(test.getId())
                .name(test.getName())
                .createdAt(test.getCreatedAt())
                .build();
    }

    public Test toTest(String name){
        Test test = Test.builder()
                .name(name)
                .build();

        return test;
    }
}
