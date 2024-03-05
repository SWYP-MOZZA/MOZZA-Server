package shop.mozza.app.login.oauth.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;


@RequiredArgsConstructor
@Getter
public class OAuth2Response {


    private final Map<String, Object> attribute;

    String id;
    String email;
    String nickName;

}
