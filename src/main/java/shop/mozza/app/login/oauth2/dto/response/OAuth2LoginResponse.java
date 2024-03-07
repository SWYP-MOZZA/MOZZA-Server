package shop.mozza.app.login.oauth2.dto.response;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OAuth2LoginResponse {
    private int statusCode;
    private String accessToken;
    private String refreshToken;
    private int expiresIn;
    private long userId;
    private String userName;
//    private String userEmail;
}