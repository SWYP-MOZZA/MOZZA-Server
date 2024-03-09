package shop.mozza.app.login.oauth2.dto.response;

import lombok.Data;

@Data
public class KakaoUserInfoResponse {
    private Long id;
    private String connected_at;
    private KakaoAccount kakao_account;
}
