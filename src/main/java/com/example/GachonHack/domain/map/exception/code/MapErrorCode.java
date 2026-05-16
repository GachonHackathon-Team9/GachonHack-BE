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
            "해당 공간을 찾을 수 없습니다."),
    INVALID_COORDINATES(HttpStatus.BAD_REQUEST,
            "MAP400_1",
            "목표 좌표(targetX, targetY)는 필수입니다."),
    INVALID_SPACE_TYPE(HttpStatus.BAD_REQUEST,
            "MAP400_2",
            "요청한 공간 유형이 올바르지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
