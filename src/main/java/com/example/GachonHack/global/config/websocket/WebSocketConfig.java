package com.example.GachonHack.global.config.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Arrays;

/**
 * Spring WebSocket + STOMP 메시지 브로커 전역 설정 클래스.
 * <p>
 * 이 프로젝트의 "실시간" 기능(맵 캐릭터 위치, 공간 채팅)은 REST API 폴링이 아니라
 * WebSocket 위에서 STOMP 프로토콜을 사용해 구현합니다.
 * </p>
 *
 * <h2>용어 정리</h2>
 * <ul>
 *   <li><b>WebSocket</b> — 클라이언트와 서버가 한 번 연결을 맺은 뒤, HTTP처럼 요청할 때마다
 *       연결을 새로 맺지 않고 양방향으로 데이터를 주고받는 전송 계층입니다.</li>
 *   <li><b>STOMP</b> (Simple Text Oriented Messaging Protocol) — WebSocket 위에서
 *       "어디로 보낼지(/app/...)", "무엇을 구독할지(/topic/...)"를 표준화한 메시징 규약입니다.
 *       CONNECT, SUBSCRIBE, SEND 같은 프레임 타입으로 동작합니다.</li>
 *   <li><b>SockJS</b> — WebSocket을 지원하지 않는 네트워크/브라우저 환경을 위해
 *       WebSocket과 유사한 동작을 HTTP long-polling 등으로 흉내 내는 fallback 라이브러리입니다.
 *       프론트에서 {@code new SockJS('/ws/spaces')} 형태로 접속합니다.</li>
 * </ul>
 *
 * <h2>메시지 경로 규칙 (이 클래스에서 정의)</h2>
 * <pre>
 * [클라이언트 SEND]  /app/...     → 서버의 @MessageMapping 핸들러로 라우팅 (애플리케이션 목적지)
 * [서버 브로드캐스트] /topic/...   → 해당 토픽을 SUBSCRIBE 한 모든 클라이언트에게 전달 (브로커 목적지)
 * </pre>
 *
 * <h2>엔드포인트 2개를 둔 이유</h2>
 * <ul>
 *   <li>{@code /ws/spaces} — 에공 복도 맵 등에서 <b>캐릭터 좌표</b> 실시간 동기화
 *       ({@link com.example.GachonHack.domain.map.controller.SpaceWsController})</li>
 *   <li>{@code /ws/chat} — 강의실·스터디룸 <b>공간 채팅</b> 실시간 송수신
 *       ({@link com.example.GachonHack.domain.community.controller.ChatWsController})</li>
 * </ul>
 * URL만 다르고 STOMP 목적지 규칙(/app, /topic)은 동일합니다. 프론트는 용도에 맞는 URL로 연결하면 됩니다.
 *
 * <h2>인증과의 관계</h2>
 * HTTP {@code SecurityConfig}에서 {@code /ws/**}는 핸드셰이크(최초 연결)를 위해 permitAll 입니다.
 * 실제 로그인 검증은 STOMP CONNECT 프레임 시점에
 * {@link StompJwtChannelInterceptor}가 JWT를 검사하여 수행합니다.
 *
 * @see StompJwtChannelInterceptor
 * @see org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompJwtChannelInterceptor stompJwtChannelInterceptor;

    @Value("${app.websocket.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    /**
     * 인메모리 메시지 브로커(간단 브로커)의 목적지 prefix를 설정합니다.
     * <p>
     * {@code enableSimpleBroker("/topic")} — 서버가 클라이언트에게 "뿌려주는" 구독 채널의 prefix.
     * 예: 클라이언트가 {@code SUBSCRIBE /topic/spaces/3} 하면, 서버의
     * {@code @SendTo("/topic/spaces/{spaceId}")} 결과가 그 구독자들에게 전달됩니다.
     * </p>
     * <p>
     * {@code setApplicationDestinationPrefixes("/app")} — 클라이언트가 "서버 로직으로 보내는"
     * SEND 프레임의 prefix. 예: {@code SEND /app/spaces/3/move-to} →
     * {@code @MessageMapping("/spaces/{spaceId}/move-to")} 메서드가 실행됩니다.
     * (/app 은 브로커 설정상 자동으로 제거되고 나머지 경로만 매핑에 사용됩니다.)
     * </p>
     * <p>
     * 운영에서 트래픽이 크면 RabbitMQ 등 외부 브로커로 교체할 수 있으나,
     * 해커톤/단일 인스턴스에서는 SimpleBroker로 충분한 경우가 많습니다.
     * </p>
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * 클라이언트가 최초로 WebSocket(또는 SockJS) 연결을 맺을 때 사용하는 HTTP 엔드포인트를 등록합니다.
     * <p>
     * 이 단계는 아직 STOMP가 아니라 "전화선을 거는" 과정에 가깝습니다.
     * 연결이 성공한 뒤 클라이언트는 STOMP CONNECT 프레임을 보내고,
     * 그때 {@link StompJwtChannelInterceptor}가 JWT를 검증합니다.
     * </p>
     * <ul>
 *   <li>{@code app.websocket.allowed-origins} — CSWSH 방지를 위해 와일드카드(*) 대신
 *       허용 프론트 도메인 화이트리스트만 등록합니다.</li>
     *   <li>{@code withSockJS()} — 순수 WebSocket 실패 시 SockJS fallback 활성화.</li>
     * </ul>
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/spaces", "/ws/chat")
                .setAllowedOriginPatterns(Arrays.stream(allowedOrigins.split(","))
                        .map(String::trim)
                        .filter(origin -> !origin.isEmpty())
                        .toArray(String[]::new))
                .withSockJS();
    }

    /**
     * 서버로 <b>들어오는</b> STOMP 메시지 채널에 인터셉터를 등록합니다.
     * <p>
     * {@link ChannelInterceptor}는 클라이언트 → 서버 방향 메시지가
     * {@code @MessageMapping}에 도달하기 <b>전</b>에 실행됩니다.
     * 여기서 CONNECT 시 JWT 인증, SUBSCRIBE/SEND 시 인증 여부 확인을 수행합니다.
     * </p>
     * <p>
     * 반대로 서버 → 클라이언트 브로드캐스트 나가는 채널(outbound)에는
     * 이 설정이 적용되지 않습니다. (인증은 이미 세션에 저장된 Principal 사용)
     * </p>
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompJwtChannelInterceptor);
    }
}
