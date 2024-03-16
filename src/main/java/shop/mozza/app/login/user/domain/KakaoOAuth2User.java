package shop.mozza.app.login.user.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import shop.mozza.app.login.user.dto.UserDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class KakaoOAuth2User implements OAuth2User {

    private final UserDto userDto;

    public KakaoOAuth2User(UserDto userDto) {

        this.userDto = userDto;
    }

    @Override
    public Map<String, Object> getAttributes() {

        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {
                return userDto.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getName() {
        return userDto.getName();
    }


    public Long getID(){return userDto.getId();}

}