package shop.mozza.app.login.oauth2.dto.response;

import java.util.Map;

public class KakaoResponse implements OAuth2Response{

    private final Map<String, Object> attribute;
    private final Map<String, Object> kakaoProfile;
    private final Map<String, Object> kakaoAccount;


    public KakaoResponse(Map<String, Object> attribute) {

        this.attribute = (Map<String, Object>) attribute.get("response");
        this.kakaoAccount= (Map<String, Object>)attribute.get("kakao_account");
        this.kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getName() {

        return kakaoProfile.get("nickname").toString();
    }
}