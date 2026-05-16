package com.example.GachonHack.domain.shop.exception.code;

import com.example.GachonHack.global.apiPayload.code.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ShopErrorCode implements BaseCode {
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND,
            "SHOP404_1",
            "상점 아이템을 찾을 수 없습니다."),
    TITLE_NOT_FOUND(HttpStatus.NOT_FOUND,
            "SHOP404_2",
            "칭호 정보를 찾을 수 없습니다."),
    NOT_ON_SALE(HttpStatus.BAD_REQUEST,
            "SHOP400_1",
            "판매 중인 아이템이 아닙니다."),
    INSUFFICIENT_POINTS(HttpStatus.BAD_REQUEST,
            "SHOP400_2",
            "포인트가 부족합니다."),
    ALREADY_OWNED(HttpStatus.BAD_REQUEST,
            "SHOP400_3",
            "이미 보유한 칭호입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,
            "SHOP404_3",
            "사용자를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
