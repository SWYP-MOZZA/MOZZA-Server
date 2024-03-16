package shop.mozza.app.login.oauth2.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import shop.mozza.app.login.oauth2.dto.response.KakaoResponse;
import shop.mozza.app.login.oauth2.dto.response.OAuth2Response;
import shop.mozza.app.login.user.domain.KakaoOAuth2User;
import shop.mozza.app.login.user.domain.User;
import shop.mozza.app.login.user.dto.UserDto;
import shop.mozza.app.login.user.repository.UserRepository;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor

public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("oAuth2User");

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;


        oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        Long userId = Long.parseLong(oAuth2Response.getProviderId());
        String username = oAuth2Response.getName();

        Optional<User> exitsUser = userRepository.findById(userId);
        if (exitsUser.isEmpty()) {
            User newUser = User.builder()
                    .name(username)
                    .isMember(true)
                    .role("USER")
                    .oauthId(userId)
                    .build();
            userRepository.save(newUser);
            UserDto userDto = UserDto.from(newUser);
            return new KakaoOAuth2User(userDto);
        }

        else {

            exitsUser.get().updateUserName(oAuth2Response.getName());
            userRepository.save(exitsUser.get());
            UserDto userDTO = UserDto.from(exitsUser.get());
            return new KakaoOAuth2User(userDTO);
        }
    }

}
