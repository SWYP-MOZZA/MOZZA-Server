package shop.mulmagi.app.web.controller;

import shop.mulmagi.app.converter.TestConverter;
import shop.mulmagi.app.exception.ResponseMessage;
import shop.mulmagi.app.exception.StatusCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.mulmagi.app.domain.Test;
import shop.mulmagi.app.exception.CustomExceptions;
import shop.mulmagi.app.service.TestService;
import shop.mulmagi.app.web.controller.base.BaseController;
import shop.mulmagi.app.web.dto.TestRequestDto;
import shop.mulmagi.app.web.dto.TestResponseDto;
import shop.mulmagi.app.web.dto.base.DefaultRes;

@Api(tags = "테스트 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController extends BaseController {
    private final TestService testService;
    private final TestConverter testConverter;

    @ApiOperation(value = "테스트 API")
    @ApiResponse(code = 200, message = "테스트 성공")
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
