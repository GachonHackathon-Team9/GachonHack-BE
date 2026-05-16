package com.example.GachonHack.global.auth.service;

import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.domain.user.exception.UserException;
import com.example.GachonHack.domain.user.exception.code.UserErrorCode;
import com.example.GachonHack.domain.user.repository.UserRepository;
import com.example.GachonHack.global.auth.entity.RefreshToken;
import com.example.GachonHack.global.auth.exception.AuthException;
import com.example.GachonHack.global.auth.exception.code.AuthErrorCode;
import com.example.GachonHack.global.auth.repository.RefreshTokenRepository;
import com.example.GachonHack.global.config.security.jwt.JwtUtil;
import com.example.GachonHack.global.util.TokenHashUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public Map<String, String> reissue(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new AuthException(AuthErrorCode.NOT_FOUND_REFRESH_TOKEN);
        }

        Claims claims = jwtUtil.validateToken(refreshToken);
        Long userId = Long.valueOf(claims.getSubject());

        RefreshToken saved = refreshTokenRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.NOT_FOUND_REFRESH_TOKEN));

        if (!saved.getTokenHash().equals(TokenHashUtil.hash(refreshToken))) {
            throw new AuthException(AuthErrorCode.NOT_FOUND);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        String newAccessToken = jwtUtil.createAccessToken(userId, user.getRole().name());
        String newRefreshToken = jwtUtil.createRefreshToken(userId);
        saved.updateTokenHash(TokenHashUtil.hash(newRefreshToken));

        return Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken
        );
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) return;
        try {
            Claims claims = jwtUtil.validateToken(refreshToken);
            Long userId = Long.valueOf(claims.getSubject());
            refreshTokenRepository.deleteById(userId);
        } catch (Exception ignored) {
            // 만료/변조된 토큰도 로그아웃 처리 진행
        }
    }
}
