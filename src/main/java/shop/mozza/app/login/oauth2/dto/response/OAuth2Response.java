package shop.mozza.app.login.oauth2.dto.response;


public interface OAuth2Response {

    //제공자에서 발급해주는 아이디(번호)
    String getProviderId();

    String getName();
}