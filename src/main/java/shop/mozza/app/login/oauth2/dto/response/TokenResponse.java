package shop.mozza.app.login.oauth2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;


@Data
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private Date expiresAt;
}
