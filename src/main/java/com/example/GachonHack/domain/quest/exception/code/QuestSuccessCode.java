package com.example.GachonHack.domain.quest.exception.code;

import com.example.GachonHack.global.apiPayload.code.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum QuestSuccessCode implements BaseCode {
    DAILY_LIST_SUCCESS(HttpStatus.OK,
            "QUEST200_1",
            "일일 퀘스트 목록 조회에 성공했습니다."),
    VERIFY_SUCCESS(HttpStatus.OK,
            "QUEST200_2",
            "퀘스트 완료 신청에 성공했습니다."),
    REWARD_SUCCESS(HttpStatus.OK,
            "QUEST200_3",
            "퀘스트 검수 처리에 성공했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
