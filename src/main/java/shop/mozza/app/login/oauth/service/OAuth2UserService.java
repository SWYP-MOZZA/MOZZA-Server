package shop.mozza.app.login.oauth.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mozza.app.user.domain.User;
import shop.mozza.app.user.domain.UserOAuth;
import shop.mozza.app.user.repository.UserOAuthRepository;
import shop.mozza.app.user.repository.UserRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {


    private final UserOAuthRepository userOAuthRepository;
    private final UserRepository userRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map <String, Object> attributes = oAuth2User.getAttributes();
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("USER");
        User user = saveOrUpdate(attributes);
        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), user.getName());
    }


    private User saveOrUpdate(Map<String, Object> attributes) {

//        String email = (String) attributes.get("email");
        String name = (String) attributes.get("nickname");

        User user = userRepository.findByName(name);
        UserOAuth userOAuth = null;
        if (user == null) {
            // 신규 회원
            user = User.builder()
                    .name(name)
                    .isMember(true)
                    .build();

            userOAuth= UserOAuth.builder()
                    .user(user)
//                    .email(email)
                    .build();
            log.debug("신규 회원 생성 : " + user.getName());

        }
        else {
            // 기존 회원
            userOAuth = userOAuthRepository.findByUser(user);
            user.setName(name);
//            userOAuth.setEmail(email);
            log.debug("기존 회원 업데이트 : " + user.getName());
        }
        userRepository.save(user);
        userOAuthRepository.save(userOAuth);
        return user;
    }
}

// nameAttributeKey
//        String userNameAttributeName = userRequest.getClientRegistration()
//                .getProviderDetails()
//                .getUserInfoEndpoint()
//                .getUserNameAttributeName();