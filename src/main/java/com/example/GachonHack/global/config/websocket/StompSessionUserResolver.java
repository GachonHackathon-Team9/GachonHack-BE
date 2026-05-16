package com.example.GachonHack.global.config.websocket;

import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.global.auth.CustomUserDetails;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * STOMP 세션에서 로그인 {@link User}를 안전하게 꺼내는 유틸.
 * {@link StompJwtChannelInterceptor}가 CONNECT 시 principal에 {@link CustomUserDetails}를 설정합니다.
 */
public final class StompSessionUserResolver {

    private StompSessionUserResolver() {
    }

    public static User resolve(SimpMessageHeaderAccessor headerAccessor) {
        Object sessionUser = headerAccessor.getUser();
        if (sessionUser instanceof UsernamePasswordAuthenticationToken auth) {
            Object principal = auth.getPrincipal();
            if (principal instanceof CustomUserDetails details) {
                return details.getUser();
            }
            if (principal instanceof User user) {
                return user;
            }
            throw new IllegalStateException(
                    "Principal이 User 타입이 아닙니다: " + principal.getClass().getName()
            );
        }
        throw new IllegalStateException("인증된 사용자가 필요합니다.");
    }
}
