package shop.mozza.app.login.user.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import shop.mozza.app.login.user.dto.UserDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor

public class GuestUser  {

    private final UserDto userDto;

    public Map<String, Object> getAttributes() {

        return null;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            public String getAuthority() {
                return userDto.getRole();
            }
        });

        return collection;
    }

    public String getName() {
        return userDto.getName();
    }

}
