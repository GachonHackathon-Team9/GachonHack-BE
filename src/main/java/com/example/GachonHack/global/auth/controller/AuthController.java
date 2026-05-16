package com.example.GachonHack.global.auth.controller;

import com.example.GachonHack.global.apiPayload.ApiResponse;
import com.example.GachonHack.global.auth.exception.code.AuthSuccessCode;
import com.example.GachonHack.global.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;

    @Override
    @GetMapping("/login/kakao")
    public void kakaoLoginInfo() {}

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        return authService.reissue(request, response);
    }

    @Override
    @PostMapping("/logout")
    public ApiResponse<?> logout(HttpServletResponse response) {
        authService.logout(response);
        return ApiResponse.onSuccess(AuthSuccessCode.LOGOUT_SUCCESS, null);
    }
}
