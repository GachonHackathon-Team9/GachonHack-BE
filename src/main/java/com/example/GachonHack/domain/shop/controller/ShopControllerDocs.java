package com.example.GachonHack.domain.shop.controller;

import com.example.GachonHack.domain.shop.dto.req.ShopRequestDTO;
import com.example.GachonHack.domain.shop.dto.res.ShopResponseDTO;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Shop", description = "상점 API (칭호)")
public interface ShopControllerDocs {

    @Operation(summary = "상점 아이템 목록", description = "판매 중인 칭호 목록을 조회합니다.")
    ApiResponse<ShopResponseDTO.ShopItemListResDTO> getItems(@AuthenticationPrincipal User user);

    @Operation(summary = "아이템 구매", description = "칭호를 포인트로 구매합니다.")
    ApiResponse<ShopResponseDTO.PurchaseResDTO> purchase(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ShopRequestDTO.PurchaseReqDTO request
    );
}
