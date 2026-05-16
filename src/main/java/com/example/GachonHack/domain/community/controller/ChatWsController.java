package com.example.GachonHack.domain.community.controller;

import com.example.GachonHack.domain.community.dto.req.ChatRequestDTO;
import com.example.GachonHack.domain.community.dto.res.ChatResponseDTO;
import com.example.GachonHack.domain.community.service.ChatService;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.global.config.websocket.StompSessionUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

/**
 * 특정 공간(강의실, 아르테크네 등) 안 <b>실시간 채팅</b>을 처리하는 STOMP 컨트롤러.
 * <p>
 * 유저 플로우상 "특정 공간 내부 화면" 하단 채팅창에서 메시지를 입력·전송할 때 사용합니다.
 * 게시판/댓글은 REST {@code /api/posts} 이고, 채팅만 WebSocket으로 분리한 이유는
 * <b>같은 방에 있는 사용자에게 즉시</b> 보여 주기 위함입니다.
 * </p>
 *
 * <h2>연결 URL</h2>
 * {@code new SockJS('.../ws/chat')} — 좌표 동기화({@code /ws/spaces})와 엔드포인트만 다릅니다.
 *
 * <h2>프론트엔드 사용 순서 (예시)</h2>
 * <pre>
 * 1. /ws/chat 로 STOMP 연결 + CONNECT (JWT)
 * 2. SUBSCRIBE /topic/chat/{spaceId}
 * 3. SEND /app/chat/{spaceId}/send  body: {"body":"안녕하세요"}
 * 4. MESSAGE 수신 — MessageBroadcastDTO (닉네임, 내용, 시각 등)
 * </pre>
 *
 * <h2>DB 전제 조건</h2>
 * 해당 {@code spaceId}에 연결된 {@link com.example.GachonHack.domain.community.entity.ChatRoom}
 * (active=true)가 DB에 있어야 합니다. 없으면 {@code CHAT_ROOM_NOT_FOUND}.
 *
 * <h2>저장소</h2>
 * {@link com.example.GachonHack.domain.community.entity.ChatMessage} (테이블 chat_messages)
 *
 * @see com.example.GachonHack.global.config.websocket.WebSocketConfig
 * @see com.example.GachonHack.global.config.websocket.StompJwtChannelInterceptor
 * @see com.example.GachonHack.domain.community.service.ChatService
 */
@Controller
@RequiredArgsConstructor
public class ChatWsController {

    private final ChatService chatService;

    /**
     * 공간 채팅 메시지 전송: DB 저장 후 같은 공간 구독자에게 브로드캐스트.
     *
     * <p><b>클라이언트 SEND</b>: {@code /app/chat/{spaceId}/send}</p>
     * <p><b>구독·수신</b>: {@code /topic/chat/{spaceId}}</p>
     *
     * @param spaceId        채팅이 속한 공간 PK (강의실·스터디룸 등)
     * @param request        메시지 본문 ({@code body} 필드)
     * @param headerAccessor CONNECT 시 인증된 사용자
     * @return 저장된 메시지 정보 + 작성자 닉네임 (다른 클라이언트 UI 렌더링용)
     */
    @MessageMapping("/chat/{spaceId}/send")
    @SendTo("/topic/chat/{spaceId}")
    public ChatResponseDTO.MessageBroadcastDTO send(
            @DestinationVariable Long spaceId,
            ChatRequestDTO.SendMessageReqDTO request,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        User user = StompSessionUserResolver.resolve(headerAccessor);
        return chatService.sendMessage(user.getId(), spaceId, request);
    }
}
