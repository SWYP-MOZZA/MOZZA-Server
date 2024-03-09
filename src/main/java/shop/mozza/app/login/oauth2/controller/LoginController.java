package shop.mozza.app.login.oauth2.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;


@Controller
public class LoginController {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;
    @GetMapping("/kakao-login")
    public String getAccessToken(@RequestParam("code") String code) {
        //액세스 토큰 요청

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        // 2. body 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code"); //고정값
        params.add("client_id", clientId);
        params.add("redirect_uri", "http://localhost:3000/auth"); //등록한 redirect uri
        params.add("code", code);


        // 3. header + body
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, httpHeaders);

        // 4. http 요청하기
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                httpEntity,
                Object.class
        );

        System.out.println("response = " + response);


        return "home";
    }

    @GetMapping("/kakao-user-info")
    public String getKakaoUserInfo(@RequestParam("accessToken") String accessToken) {
        // 1. HttpHeaders 생성 및 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        // 2. RestTemplate 생성 및 API 요청 실행
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                entity,
                Object.class
        );

        // 3. API 응답 출력 및 처리
        System.out.println("User Info = " + response.getBody());

        // 여기에서 필요한 처리를 수행합니다. 예: 사용자 정보를 데이터베이스에 저장

        return "userInfoPage"; // 사용자 정보를 보여주는 페이지로 리디렉션
    }







}
