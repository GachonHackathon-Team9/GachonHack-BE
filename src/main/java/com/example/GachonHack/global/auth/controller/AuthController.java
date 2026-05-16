package com.example.GachonHack.global.auth.controller;

import com.example.GachonHack.global.apiPayload.ApiResponse;
import com.example.GachonHack.global.auth.exception.code.AuthSuccessCode;
import com.example.GachonHack.global.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ApiResponse<Map<String, String>> reissue(
            @RequestBody Map<String, String> body
    ) {
        return ApiResponse.onSuccess(AuthSuccessCode.REISSUE_SUCCESS, authService.reissue(body.get("refreshToken")));
    }

    @Override
    @PostMapping("/logout")
    public ApiResponse<?> logout(@RequestBody Map<String, String> body) {
        authService.logout(body.get("refreshToken"));
        return ApiResponse.onSuccess(AuthSuccessCode.LOGOUT_SUCCESS, null);
    }
}
