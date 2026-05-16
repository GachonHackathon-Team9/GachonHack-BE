package com.example.GachonHack.domain.map.controller;

import com.example.GachonHack.domain.map.dto.req.MapRequestDTO;
import com.example.GachonHack.domain.map.dto.res.MapWsResponseDTO;
import com.example.GachonHack.domain.map.service.UserPresenceService;
import com.example.GachonHack.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

/**
 * 캠퍼스 맵(에공 복도 등)에서 <b>캐릭터 위치</b>를 실시간으로 동기화하는 STOMP 컨트롤러.
 * <p>
 * REST API가 아닌 WebSocket/STOMP이므로 {@code @RestController} / {@code @GetMapping}이 아니라
 * {@code @Controller} + {@code @MessageMapping}을 사용합니다.
 * </p>
 *
 * <h2>유저 플로우와의 대응</h2>
 * <ul>
 *   <li>화면: 에공 층별 복도 맵</li>
 *   <li>동작: 사용자가 맵의 <b>특정 지점을 클릭</b> → 캐릭터가 그 좌표로 이동 (이동 애니메이션은 프론트 담당)</li>
 *   <li>서버: 클릭한 <b>목표 절대 좌표</b>(targetX, targetY)를 받아 DB 저장 후, 같은 공간 사용자에게 브로드캐스트</li>
 * </ul>
 * 방향키/조이스틱처럼 "매 프레임 델타"를 보내는 API가 아닙니다.
 *
 * <h2>연결 URL (WebSocketConfig)</h2>
 * {@code new SockJS('.../ws/spaces')} 로 연결합니다. 채팅은 {@code /ws/chat}을 사용합니다.
 *
 * <h2>프론트엔드 사용 순서 (예시)</h2>
 * <pre>
 * 1. /ws/spaces 로 SockJS + STOMP 연결
 * 2. CONNECT (JWT 헤더) — StompJwtChannelInterceptor 인증
 * 3. SUBSCRIBE /topic/spaces/{spaceId}  — 다른 유저 위치 수신
 * 4. SEND /app/spaces/{spaceId}/move-to  body: {"targetX":120.5,"targetY":340.0}
 * 5. MESSAGE 수신 (브로드캐스트) — PositionBroadcastDTO JSON
 * </pre>
 *
 * <h2>저장소</h2>
 * {@link com.example.GachonHack.domain.map.entity.UserPresence} (테이블 user_presence, ERD: 사용자 위치)
 *
 * @see com.example.GachonHack.global.config.websocket.WebSocketConfig
 * @see com.example.GachonHack.global.config.websocket.StompJwtChannelInterceptor
 * @see com.example.GachonHack.domain.map.service.UserPresenceService
 */
@Validated
@Controller
@RequiredArgsConstructor
public class SpaceWsController {

    private final UserPresenceService userPresenceService;

    /**
     * 맵 클릭 시 목표 좌표로 캐릭터 위치를 갱신하고, 같은 공간을 구독 중인 클라이언트 전체에 전파.
     *
     * <p><b>클라이언트 SEND 경로</b>: {@code /app/spaces/{spaceId}/move-to}</p>
     * <p><b>브로드캐스트 경로</b>: {@code /topic/spaces/{spaceId}}</p>
     * ({@link SendTo}가 반환값을 구독자에게 자동 발행)
     *
     * @param spaceId   공간(맵) PK — {@link com.example.GachonHack.domain.map.entity.Space#getId()}
     * @param request   클릭한 목표 좌표 (절대값, null 불가)
     * @param headerAccessor STOMP 세션 헤더 — CONNECT 시 저장된 로그인 사용자 조회용
     * @return 브로드캐스트 페이로드 (userId, nickname, spaceId, targetX, targetY)
     */
    @MessageMapping("/spaces/{spaceId}/move-to")
    @SendTo("/topic/spaces/{spaceId}")
    public MapWsResponseDTO.PositionBroadcastDTO moveToPosition(
            @DestinationVariable Long spaceId,
            @Valid MapRequestDTO.MoveToPositionReqDTO request,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        User user = resolveUser(headerAccessor);
        return userPresenceService.moveToPosition(user.getId(), spaceId, request);
    }

    /**
     * STOMP 세션에 바인딩된 Spring Security 인증 객체에서 {@link User} 엔티티 추출.
     * <p>
     * Principal은 {@link StompJwtChannelInterceptor}가 CONNECT 시 설정한
     * {@link org.springframework.security.authentication.UsernamePasswordAuthenticationToken}입니다.
     * 인증이 없으면 인터셉터 단계에서 이미 막혀야 하며, 만약 도달했다면 서버 설정 버그로 간주합니다.
     * </p>
     */
    private User resolveUser(SimpMessageHeaderAccessor headerAccessor) {
        Object user = headerAccessor.getUser();
        if (user instanceof org.springframework.security.authentication.UsernamePasswordAuthenticationToken auth) {
            return (User) auth.getPrincipal();
        }
        throw new IllegalStateException("인증된 사용자가 필요합니다.");
    }
}
