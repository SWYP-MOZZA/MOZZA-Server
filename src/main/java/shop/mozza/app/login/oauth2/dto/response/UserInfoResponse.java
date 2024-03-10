package shop.mozza.app.login.oauth2.dto.response;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserInfoResponse {

    private int statusCode;
    private String responseMessage;
    private String name;
    private String email;


    @Builder
    public UserInfoResponse(int statusCode, String responseMessage, String name, String email) {
        this.statusCode = statusCode;
        this.responseMessage = responseMessage;
        this.name = name;
        this.email = email;
    }
}
