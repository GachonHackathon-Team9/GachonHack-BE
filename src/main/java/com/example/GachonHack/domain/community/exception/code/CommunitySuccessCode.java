package com.example.GachonHack.domain.community.exception.code;

import com.example.GachonHack.global.apiPayload.code.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommunitySuccessCode implements BaseCode {
    POST_LIST_SUCCESS(HttpStatus.OK,
            "COMM200_1",
            "게시글 목록 조회에 성공했습니다."),
    POST_CREATE_SUCCESS(HttpStatus.OK,
            "COMM200_2",
            "게시글 작성에 성공했습니다."),
    POST_DETAIL_SUCCESS(HttpStatus.OK,
            "COMM200_3",
            "게시글 상세 조회에 성공했습니다."),
    MENTORING_REQUEST_CREATE_SUCCESS(HttpStatus.OK,
            "COMM200_6",
            "짝선짝후 매칭 신청에 성공했습니다."),
    MENTORING_REQUEST_LIST_SUCCESS(HttpStatus.OK,
            "COMM200_7",
            "매칭 신청 목록 조회에 성공했습니다."),
    MENTORING_REQUEST_UPDATE_SUCCESS(HttpStatus.OK,
            "COMM200_8",
            "매칭 신청 처리에 성공했습니다."),
    MENTORING_MATCH_LIST_SUCCESS(HttpStatus.OK,
            "COMM200_9",
            "내 짝꿍 목록 조회에 성공했습니다."),
    MENTORING_MATCH_CONFIRM_SUCCESS(HttpStatus.OK,
            "COMM200_10",
            "매칭 확정 신청에 성공했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
