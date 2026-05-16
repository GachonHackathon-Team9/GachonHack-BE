package com.example.GachonHack.global.config.security.oauth2;

import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.global.auth.entity.RefreshToken;
import com.example.GachonHack.global.auth.repository.RefreshTokenRepository;
import com.example.GachonHack.global.config.security.jwt.JwtUtil;
import com.example.GachonHack.global.util.CookieUtil;
import com.example.GachonHack.global.util.TokenHashUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${oauth2.redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oAuth2User.getUser();

        Long userId = user.getId();
        String role = user.getRole().name();

        String accessToken = jwtUtil.createAccessToken(userId, role);
        String refreshToken = jwtUtil.createRefreshToken(userId);
        String refreshTokenHash = TokenHashUtil.hash(refreshToken);

        refreshTokenRepository.save(RefreshToken.of(userId, refreshTokenHash));

        response.addHeader("Set-Cookie", CookieUtil.accessToken(accessToken).toString());
        response.addHeader("Set-Cookie", CookieUtil.refreshToken(refreshToken).toString());

        response.sendRedirect(redirectUri);
    }
}
