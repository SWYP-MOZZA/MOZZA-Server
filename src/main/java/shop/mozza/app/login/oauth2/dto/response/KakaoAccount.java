package shop.mozza.app.login.oauth2.dto.response;

import lombok.Data;

@Data
public class KakaoAccount {
    private String profile_nickname_needs_agreement;
    private Profile profile;
    private Boolean has_email;
    private Boolean email_needs_agreement;
    private Boolean is_email_valid;
    private Boolean is_email_verified;
    private String email;
}

