package com.example.GachonHack.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

public class CookieUtil {

    private static final int ACCESS_MAX_AGE = 60 * 60;          // 1시간
    private static final int REFRESH_MAX_AGE = 60 * 60 * 24 * 14; // 14일

    public static ResponseCookie accessToken(String token) {
        return ResponseCookie.from("accessToken", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(ACCESS_MAX_AGE)
                .sameSite("Lax")
                .build();
    }

    public static ResponseCookie refreshToken(String token) {
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(REFRESH_MAX_AGE)
                .sameSite("Lax")
                .build();
    }

    public static ResponseCookie delete(String name) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }

    public static String get(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
