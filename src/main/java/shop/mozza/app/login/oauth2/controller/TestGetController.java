package shop.mozza.app.login.oauth2.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import shop.mozza.app.login.user.domain.User;
import shop.mozza.app.login.user.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class TestGetController {
    private final UserService userService;
    @GetMapping("/test-get")
    @ResponseBody
    public Map<String, String> testGet() {

        Map<String, String> response = new HashMap<>();
        User user  = userService.getCurrentUser();

        response.put("message", user.getName());
        return response;
    }
}
