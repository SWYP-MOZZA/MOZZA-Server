package shop.mozza.app.login.oauth2.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class TestGetController {
    @GetMapping("/test-get")
    @ResponseBody
    public Map<String, String> testGet() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "testGet");
        return response;
    }
}
