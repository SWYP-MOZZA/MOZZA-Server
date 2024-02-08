package shop.mozza.app.web.controller;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import shop.mozza.app.converter.TestConverter;
import shop.mozza.app.exception.ResponseMessage;
import shop.mozza.app.exception.StatusCode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.mozza.app.domain.Test;
import shop.mozza.app.exception.CustomExceptions;
import shop.mozza.app.service.TestService;
import shop.mozza.app.web.controller.base.BaseController;
import shop.mozza.app.web.dto.TestRequestDto;
import shop.mozza.app.web.dto.TestResponseDto;
import shop.mozza.app.web.dto.base.DefaultRes;

@Tag(name = "테스트 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController extends BaseController {
    private final TestService testService;
    private final TestConverter testConverter;

    @Operation(summary = "테스트 API")
    @ApiResponse(responseCode = "200", description = "테스트 성공")
    @PostMapping
    public ResponseEntity createTest(@RequestBody TestRequestDto.CreateTestDto request){
        try {
            logger.info("Received request: method={}, path={}, description={}", "POST", "/my-profile/charge", "포인트 충전 API");

            Test test = testService.create(request.getName());
            TestResponseDto.CreateTestDto res = testConverter.toCreateTestDto(test);
            return new ResponseEntity( DefaultRes.res(StatusCode.OK, ResponseMessage.TEST_SUCCESS, res), HttpStatus.OK);
        } catch (CustomExceptions.testException e) {
            return handleApiException(e, HttpStatus.BAD_REQUEST);
        }
    }
}
