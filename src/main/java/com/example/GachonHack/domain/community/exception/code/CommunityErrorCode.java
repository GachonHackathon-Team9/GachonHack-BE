package com.example.GachonHack.domain.community.exception.code;

import com.example.GachonHack.global.apiPayload.code.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommunityErrorCode implements BaseCode {
    POST_NOT_FOUND(HttpStatus.NOT_FOUND,
            "COMM404_1",
            "게시글을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,
            "COMM404_2",
            "사용자를 찾을 수 없습니다."),
    FRESHMAN_ONLY_POST(HttpStatus.FORBIDDEN,
            "COMM403_1",
            "짝선짝후 글 작성은 새내기만 가능합니다."),
    MATCH_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND,
            "COMM404_3",
            "매칭 신청을 찾을 수 없습니다."),
    MATCH_REQUEST_FORBIDDEN(HttpStatus.FORBIDDEN,
            "COMM403_4",
            "해당 매칭 신청을 처리할 권한이 없습니다."),
    DUPLICATE_MATCH_REQUEST(HttpStatus.BAD_REQUEST,
            "COMM400_2",
            "이미 진행 중인 매칭 신청이 있습니다."),
    INVALID_MATCH_STATUS(HttpStatus.BAD_REQUEST,
            "COMM400_3",
            "승인 또는 거절만 가능합니다."),
    SELF_MATCH_NOT_ALLOWED(HttpStatus.BAD_REQUEST,
            "COMM400_4",
            "본인에게 매칭 신청할 수 없습니다."),
    CHAT_MESSAGE_EMPTY(HttpStatus.BAD_REQUEST,
            "COMM400_5",
            "채팅 메시지 내용은 필수입니다."),
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND,
            "COMM404_4",
            "해당 공간의 채팅방을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
