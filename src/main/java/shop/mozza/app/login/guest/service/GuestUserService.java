//package shop.mozza.app.login.guest.service;
//
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//import org.springframework.web.server.ResponseStatusException;
//import shop.mozza.app.login.oauth2.dto.response.KakaoResponse;
//import shop.mozza.app.login.oauth2.dto.response.OAuth2Response;
//import shop.mozza.app.login.user.domain.KakaoOAuth2User;
//import shop.mozza.app.login.user.domain.User;
//import shop.mozza.app.login.user.dto.UserDto;
//import shop.mozza.app.login.user.repository.UserRepository;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//
//public class GuestUserService extends DefaultOAuth2UserService {
//
//    private final UserRepository userRepository;
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//        log.info("oAuth2User");
//
//        String registrationId = userRequest.getClientRegistration().getRegistrationId();
//        OAuth2Response oAuth2Response = null;
//
//
//        oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
//
//        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
//        String username = oAuth2Response.getName();
//
//        User exitsUser = userRepository.findByName(username);
//        if (exitsUser == null) {
//            User newUser = User.builder()
//                    .name(username)
//                    .isMember(true)
//                    .role("USER")
//                    .build();
//            userRepository.save(newUser);
//            UserDto userDto = UserDto.from(newUser);
//            return new KakaoOAuth2User(userDto);
//        }
//
//        else {
//
//            exitsUser.updateUserName(oAuth2Response.getName());
//            userRepository.save(exitsUser);
//            UserDto userDTO = UserDto.from(exitsUser);
//            return new KakaoOAuth2User(userDTO);
//        }
//    }
//
//    public User getCurrentUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated()) {
//            throw new ResponseStatusException(
//                    HttpStatus.UNAUTHORIZED, "로그인 되지 않았습니다."
//            );
//        }
//
//        // Assuming the principal can be cast to a KakaoOAuth2User or similar that contains the username
//        Object principal = authentication.getPrincipal();
//        String username;
//
//        if (principal instanceof KakaoOAuth2User) { // Or any other implementation you use
//            username = ((KakaoOAuth2User) principal).getName();
//        } else if (principal instanceof String) {
//            username = (String) principal; // In case the principal is a String
//        } else {
//            throw new IllegalStateException("Unexpected principal type");
//        }
//
//        User user = userRepository.findByName(username); // Adjust this line to match your repository method
//
//        if (user == null) {
//            throw new UsernameNotFoundException("User not found with username: " + username);
//        }
//
////        if (user.getStatus().equals(UserStatus.INACTIVE)) {
////            throw new IllegalArgumentException("해당 사용자는 탈퇴한 사용자입니다.");
////        }
//        return user;
//    }
//
//
//}
