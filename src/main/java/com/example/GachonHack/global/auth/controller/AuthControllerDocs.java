package com.example.GachonHack.global.auth.controller;

import com.example.GachonHack.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Tag(name = "Auth", description = "인증 관련 API")
public interface AuthControllerDocs {

    @GetMapping("/login/kakao")
    @Operation(
            summary = "카카오 로그인 (브라우저 리다이렉트)",
            description = """
    이 엔드포인트는 API 호출용이 아닙니다.

    프론트엔드에서 로그인 버튼 클릭 시
    아래 URL로 페이지 이동(redirect)시키면 카카오 로그인이 시작됩니다.

    [개발 환경]
    http://localhost:8080/oauth2/authorization/kakao
    [운영 환경]
    https://gachonhack-be.onrender.com/oauth2/authorization/kakao

    로그인 성공 시 아래 URL로 리다이렉트됩니다:
    {OAUTH2_REDIRECT_URI}/oauth2/redirect?token={accessToken}&refreshToken={refreshToken}
    """
    )
    default void kakaoLoginInfo() {}

    @Operation(
            summary = "Access Token 재발급",
            description = """
    만료된 Access Token을 Refresh Token으로 재발급합니다.

    **Request Body**
    ```json
    { "refreshToken": "eyJhbGci..." }
    ```

    **Response Body**
    ```json
    {
      "accessToken": "eyJhbGci...",
      "refreshToken": "eyJhbGci..."
    }
    ```
    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "재발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Refresh Token 유효하지 않음")
    })
    @PostMapping("/refresh")
    ApiResponse<Map<String, String>> reissue(@RequestBody Map<String, String> body);

    @Operation(
            summary = "로그아웃",
            description = """
    **Request Body**
    ```json
    { "refreshToken": "eyJhbGci..." }
    ```
    서버 측 Refresh Token을 무효화합니다.
    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @PostMapping("/logout")
    ApiResponse<?> logout(@RequestBody Map<String, String> body);
}
