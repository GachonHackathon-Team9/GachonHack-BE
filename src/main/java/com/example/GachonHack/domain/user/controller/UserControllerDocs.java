package com.example.GachonHack.domain.user.controller;

import com.example.GachonHack.domain.user.dto.req.UserRequestDTO;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "User", description = "유저 관련 API")
public interface UserControllerDocs {
    @Operation(
            summary = "온보딩 기능 API",
            description = """
            유저의 실명, 학번, 학년, 고유 닉네임을 저장합니다.
            """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "온보딩 저장 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "온보딩 저장 실패")
    })
    ApiResponse<Void> createUserOnboarding(@AuthenticationPrincipal User user, @Valid @RequestBody UserRequestDTO.OnboardingReqDTO request);

}
