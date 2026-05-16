package com.example.GachonHack.domain.quest.exception.code;

import com.example.GachonHack.global.apiPayload.code.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum QuestErrorCode implements BaseCode {
    QUEST_NOT_FOUND(HttpStatus.NOT_FOUND,
            "QUEST404_1",
            "퀘스트를 찾을 수 없습니다."),
    QUEST_NOT_ACTIVE(HttpStatus.BAD_REQUEST,
            "QUEST400_1",
            "진행 중인 퀘스트가 아닙니다."),
    SUBMISSION_NOT_FOUND(HttpStatus.NOT_FOUND,
            "QUEST404_2",
            "검수 대기 중인 제출 내역이 없습니다."),
    SUBMISSION_ALREADY_EXISTS(HttpStatus.BAD_REQUEST,
            "QUEST400_2",
            "이미 완료 신청했거나 검수 중인 퀘스트입니다."),
    REWARD_ALREADY_GRANTED(HttpStatus.BAD_REQUEST,
            "QUEST400_3",
            "이미 포인트가 지급되었습니다."),
    ADMIN_ONLY(HttpStatus.FORBIDDEN,
            "QUEST403_1",
            "관리자만 처리할 수 있습니다."),
    INVALID_REWARD_ACTION(HttpStatus.BAD_REQUEST,
            "QUEST400_4",
            "승인 또는 거절만 가능합니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,
            "QUEST404_3",
            "사용자를 찾을 수 없습니다."),
    MATCH_REQUIRED(HttpStatus.BAD_REQUEST,
            "QUEST400_5",
            "짝선짝후 매칭이 필요합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
