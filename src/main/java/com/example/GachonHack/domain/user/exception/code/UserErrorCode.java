package com.example.GachonHack.domain.user.exception.code;

import com.example.GachonHack.global.apiPayload.code.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements BaseCode {
    NOT_FOUND(HttpStatus.NOT_FOUND,
            "USER404_1",
            "해당 사용자를 찾을 수 없습니다."),
    TITLE_NOT_OWNED(HttpStatus.BAD_REQUEST,
            "USER400_1",
            "보유하지 않은 칭호입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
