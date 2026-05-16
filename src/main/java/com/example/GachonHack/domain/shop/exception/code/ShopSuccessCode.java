package com.example.GachonHack.domain.shop.exception.code;

import com.example.GachonHack.global.apiPayload.code.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ShopSuccessCode implements BaseCode {
    ITEM_LIST_SUCCESS(HttpStatus.OK,
            "SHOP200_1",
            "상점 아이템 목록 조회에 성공했습니다."),
    PURCHASE_SUCCESS(HttpStatus.OK,
            "SHOP200_2",
            "아이템 구매에 성공했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
