package shop.mozza.app.login.oauth2.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class refreshTokenResponse {
    private int statusCode;
    private String accessToken;
    private long expiresln;


    @Builder
    public refreshTokenResponse(int statusCode, String accessToken, long expiresln) {
        this.statusCode = statusCode;
        this.accessToken = accessToken;
        this.expiresln = expiresln;
    }
}