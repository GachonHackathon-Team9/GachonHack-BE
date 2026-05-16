package com.example.GachonHack.domain.user.controller;

import com.example.GachonHack.domain.user.dto.req.UserRequestDTO;
import com.example.GachonHack.domain.user.dto.res.UserResponseDTO;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.domain.user.exception.code.UserSuccessCode;
import com.example.GachonHack.domain.user.service.UserService;
import com.example.GachonHack.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {

    private final UserService userService;

    @Override
    @PostMapping({"/api/onboarding", "/user/onboarding"})
    public ApiResponse<Void> createUserOnboarding(
            @AuthenticationPrincipal(expression = "user") User user,
            @Valid @RequestBody UserRequestDTO.OnboardingReqDTO request
    ) {
        userService.createUserOnboarding(user.getId(), request);
        return ApiResponse.onSuccess(UserSuccessCode.ONBOARDING_SUCCESS, null);
    }

    @Override
    @GetMapping({"/api/users/me", "/users/me"})
    public ApiResponse<UserResponseDTO.MyPageResDTO> getMyPage(
            @AuthenticationPrincipal(expression = "user") User user
    ) {
        return ApiResponse.onSuccess(UserSuccessCode.MYPAGE_SUCCESS, userService.getMyPage(user.getId()));
    }

    @Override
    @PatchMapping({"/api/users/me/equipment", "/users/me/equipment"})
    public ApiResponse<Void> updateEquipment(
            @AuthenticationPrincipal(expression = "user") User user,
            @Valid @RequestBody UserRequestDTO.EquipmentUpdateReqDTO request
    ) {
        userService.updateEquipment(user.getId(), request);
        return ApiResponse.onSuccess(UserSuccessCode.EQUIPMENT_UPDATE_SUCCESS, null);
    }
}
