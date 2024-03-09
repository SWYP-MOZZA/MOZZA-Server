package shop.mozza.app.login.oauth2.dto.response;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OAuth2LoginResponse {
    private int statusCode;
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private long userId;
    private String userName;


    @Builder
    public OAuth2LoginResponse(int statusCode, String accessToken, String refreshToken, long expiresIn, long userId, String userName) {
        this.statusCode = statusCode;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.userId = userId;
        this.userName = userName;
    }
    //    private String userEmail;
}