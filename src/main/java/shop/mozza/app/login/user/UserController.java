package shop.mozza.app.login.user;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import shop.mozza.app.login.oauth2.dto.response.UserInfoResponse;
import shop.mozza.app.login.user.domain.User;
import shop.mozza.app.login.user.repository.UserRepository;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserInfoResponse> getUserById(@PathVariable String userId) {
        // 여기에서 사용자 정보를 조회하는 로직을 구현합니다.
        // 예를 들어, userId를 이용하여 데이터베이스에서 사용자 정보를 조회할 수 있습니다.
        // 임시로 사용자 ID를 반환하는 예제 코드를 작성해봅니다.

        User user = userRepository.findById(Long.parseLong(userId));
        // UserResponseDto 객체 생성 및 설정
        UserInfoResponse userResponse = UserInfoResponse
                .builder()
                .email(user.getEmail())
                .statusCode(200)
                .name(user.getName())
                .responseMessage("회원 정보를 성공적으로 가져왔습니다.")
                .build();
        return ResponseEntity.ok(userResponse);
    }
}