package com.example.GachonHack.domain.map.exception.code;

import com.example.GachonHack.global.apiPayload.code.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MapSuccessCode implements BaseCode {
    TIMETABLE_SUCCESS(HttpStatus.OK,
            "MAP200_1",
            "강의실 시간표 조회에 성공했습니다."),
    FLOOR_LIST_SUCCESS(HttpStatus.OK,
            "MAP200_2",
            "층 목록 조회에 성공했습니다."),
    CLASSROOM_LIST_SUCCESS(HttpStatus.OK,
            "MAP200_3",
            "강의실 목록 조회에 성공했습니다."),
    CLASSROOM_DETAIL_SUCCESS(HttpStatus.OK,
            "MAP200_4",
            "강의실 상세(시간표·수업 채팅방) 조회에 성공했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
