package shop.mozza.app.login.oauth.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shop.mozza.app.login.oauth.dto.request.RequestLoginDto;
import shop.mozza.app.login.oauth.service.OAuth2UserService;

@Controller
@RequestMapping("/login")
public class OAuth2LoginController {

    //redirect uri에 전달된 코드 값을 가지고 Access Token을 요청한다.
//    private final OAuth2UserService oAuth2UserService;
//    @PostMapping("/login/asdfhdsjaifh")
//    public ResponseEntity<String> getMemberProfile(
//            @Valid @RequestBody RequestLoginDto request
//    ) {
//        String token = this.oAuth2UserService.login(request);
//        return ResponseEntity.status(HttpStatus.OK).body(token);
//    }
//
    public String loginPage() {
        return "login";
    }

}
