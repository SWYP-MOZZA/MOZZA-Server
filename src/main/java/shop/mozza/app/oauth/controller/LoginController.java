package shop.mozza.app.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
public class LoginController {
    //redirect uri에 전달된 코드 값을 가지고 Access Token을 요청한다.
    @GetMapping
    public String login() {
        return "login";
    }
}
