package shop.mozza.app.login.oauth2.util;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import shop.mozza.app.login.oauth2.dto.response.KakaoUserInfoResponse;


@RequiredArgsConstructor
@Component
@Slf4j
public class KakaoUserInfo {
    private final WebClient webClient;
    private static final String USER_INFO_URI =  "https://kapi.kakao.com/v2/user/me";

    public KakaoUserInfoResponse getUserInfo(String token) {
        String baseUrl = USER_INFO_URI;

        String queryParameters = "property_keys=[\"kakao_account.profile\", \"kakao_account.email\"]";
        String urlString = baseUrl + "?" + queryParameters;

        Flux<KakaoUserInfoResponse> response = webClient.get()
                .uri(urlString)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToFlux(KakaoUserInfoResponse.class);

        return response.blockFirst();
    }
}
