package shop.mozza.app.login.oauth2.dto.response;

import lombok.Data;

@Data
public class KakaoAccount {
    private String profile_nickname_needs_agreement;
    private Profile profile;
}

