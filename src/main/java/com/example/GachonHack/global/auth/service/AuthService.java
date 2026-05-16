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
import com.example.GachonHack.global.util.CookieUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    // AccessToken 재발급
    @Transactional
    public ResponseEntity<?> reissue(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = CookieUtil.get(request, "refreshToken");
        if (refreshToken == null) {
            throw new AuthException(AuthErrorCode.NOT_FOUND_REFRESH_TOKEN);
        }

        Claims claims = jwtUtil.validateToken(refreshToken);
        Long userId = Long.valueOf(claims.getSubject());

        RefreshToken saved = refreshTokenRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.NOT_FOUND_REFRESH_TOKEN));

        if (!saved.getToken().equals(refreshToken)) {
            throw new AuthException(AuthErrorCode.NOT_FOUND);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        String newAccess = jwtUtil.createAccessToken(userId, user.getRole().name());
        String newRefresh = jwtUtil.createRefreshToken(userId);
        saved.updateToken(newRefresh);

        response.addHeader("Set-Cookie", CookieUtil.accessToken(newAccess).toString());
        response.addHeader("Set-Cookie", CookieUtil.refreshToken(newRefresh).toString());

        return ResponseEntity.ok().build();
    }

    @Transactional
    public void logout(HttpServletResponse response) {
        response.addHeader("Set-Cookie", CookieUtil.delete("accessToken").toString());
        response.addHeader("Set-Cookie", CookieUtil.delete("refreshToken").toString());
    }
}
