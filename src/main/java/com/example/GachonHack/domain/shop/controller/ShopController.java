package com.example.GachonHack.domain.shop.controller;

import com.example.GachonHack.domain.shop.dto.req.ShopRequestDTO;
import com.example.GachonHack.domain.shop.dto.res.ShopResponseDTO;
import com.example.GachonHack.domain.shop.exception.code.ShopSuccessCode;
import com.example.GachonHack.domain.shop.service.ShopService;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/shop", "/shop"})
public class ShopController implements ShopControllerDocs {

    private final ShopService shopService;

    @Override
    @GetMapping("/items")
    public ApiResponse<ShopResponseDTO.TitleItemListResDTO> getItems(
            @AuthenticationPrincipal(expression = "user") User user
    ) {
        return ApiResponse.onSuccess(ShopSuccessCode.ITEM_LIST_SUCCESS, shopService.getItems());
    }

    @Override
    @PostMapping("/purchases")
    public ApiResponse<ShopResponseDTO.PurchaseResDTO> purchase(
            @AuthenticationPrincipal(expression = "user") User user,
            @Valid @RequestBody ShopRequestDTO.PurchaseReqDTO request
    ) {
        return ApiResponse.onSuccess(
                ShopSuccessCode.PURCHASE_SUCCESS,
                shopService.purchase(user.getId(), request)
        );
    }
}
