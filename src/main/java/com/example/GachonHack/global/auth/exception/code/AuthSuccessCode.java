package com.example.GachonHack.global.auth.exception.code;

import com.example.GachonHack.global.apiPayload.code.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthSuccessCode implements BaseCode {
    REISSUE_SUCCESS(HttpStatus.OK,
            "AUTH200_1",
            "토큰 재발급에 성공했습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK,
            "AUTH200_2",
            "로그아웃을 성공했습니다.")
    ;
    private final HttpStatus status;
    private final String code;
    private final String message;
}
