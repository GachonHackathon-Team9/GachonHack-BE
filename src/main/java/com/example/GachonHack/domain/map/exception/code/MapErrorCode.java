package com.example.GachonHack.domain.map.exception.code;

import com.example.GachonHack.global.apiPayload.code.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MapErrorCode implements BaseCode {
    SPACE_NOT_FOUND(HttpStatus.NOT_FOUND,
            "MAP404_1",
            "해당 공간을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
