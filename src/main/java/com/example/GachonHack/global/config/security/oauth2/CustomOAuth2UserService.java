package com.example.GachonHack.global.config.security.oauth2;

import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (!"kakao".equals(registrationId)) {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다: " + registrationId);
        }

        Object idAttr = oAuth2User.getAttributes().get("id");
        if (idAttr == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_user_info"),
                    "Kakao 사용자 정보에 id가 없습니다.");
        }
        String socialId = idAttr.toString();

        User user = userRepository.findBySocialId(socialId)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .socialId(socialId)
                                .build()
                ));

        return new CustomOAuth2User(oAuth2User.getAttributes(), user);
    }
}
