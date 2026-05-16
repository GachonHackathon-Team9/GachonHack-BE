package com.example.GachonHack.global.config.security.jwt;

import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.domain.user.repository.UserRepository;
import com.example.GachonHack.global.auth.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/oauth2/")
                || uri.equals("/kakao/callback")
                || uri.startsWith("/swagger")
                || uri.equals("/api/auth/logout")
                || uri.equals("/api/auth/refresh")
                || uri.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException, ServletException {

        Long resolvedUserId = null;

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                var claims = jwtUtil.validateToken(authHeader.substring(7));
                String subject = claims.getSubject();
                if (subject != null && !subject.isBlank()) {
                    resolvedUserId = Long.valueOf(subject);
                }
            } catch (Exception ignored) {}
        }

        // 데모: 토큰 없거나 유효하지 않으면 userId=1 자동 주입
        if (resolvedUserId == null) {
            resolvedUserId = 1L;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userRepository.findById(resolvedUserId).orElse(null);
            if (user != null) {
                CustomUserDetails userDetails = new CustomUserDetails(user);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
