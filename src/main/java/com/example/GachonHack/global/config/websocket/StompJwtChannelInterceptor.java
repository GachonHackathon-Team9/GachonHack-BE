package com.example.GachonHack.global.config.websocket;

import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.domain.user.repository.UserRepository;
import com.example.GachonHack.global.auth.CustomUserDetails;
import com.example.GachonHack.global.config.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.Set;

/**
 * STOMP 메시지가 서버 핸들러에 도달하기 전에 JWT 인증을 강제하는 채널 인터셉터.
 * <p>
 * REST API는 {@link com.example.GachonHack.global.config.security.jwt.JwtAuthFilter}가
 * HTTP 쿠키/헤더의 JWT를 검사하지만, WebSocket은 HTTP 요청 한 번으로 끝나지 않고
 * STOMP <b>CONNECT</b> 프레임이라는 별도 handshake가 있습니다.
 * 이 클래스는 그 CONNECT 시점에 동일한 accessToken JWT를 검증해
 * "이 WebSocket 세션의 주인이 누구인지"를 {@link StompHeaderAccessor#setUser(Principal)}에 저장합니다.
 * </p>
 *
 * <h2>왜 CONNECT에서 막아야 하는가?</h2>
 * <p>
 * 인증 없이 CONNECT를 허용하면, 클라이언트는 {@code SUBSCRIBE /topic/spaces/1} 만으로
 * 다른 사용자의 좌표 브로드캐스트를 들을 수 있고, {@code SUBSCRIBE /topic/chat/1} 로
 * 채팅 메시지를 엿볼 수 있습니다. SEND는 컨트롤러에서 막혀도 <b>수신(subscribe)만으로 정보 유출</b>이
 * 가능하기 때문에 CONNECT 단계에서 반드시 거부해야 합니다.
 * </p>
 *
 * <h2>STOMP 프레임 처리 흐름</h2>
 * <pre>
 * 1) 클라이언트: WebSocket 연결 (GET /ws/spaces, SockJS)
 * 2) 클라이언트: STOMP CONNECT + 헤더(Authorization 또는 accessToken)
 *    → preSend() → authenticateConnect() → JWT 검증 → accessor.setUser(인증 객체)
 * 3) 클라이언트: STOMP SUBSCRIBE /topic/spaces/3
 *    → preSend() → requireAuthenticated() → CONNECT 때 저장한 User 있는지 확인
 * 4) 클라이언트: STOMP SEND /app/spaces/3/move-to + JSON body
 *    → preSend() → requireAuthenticated() → @MessageMapping 실행
 * </pre>
 *
 * <h2>프론트엔드가 CONNECT 시 넣어야 할 헤더</h2>
 * <ul>
 *   <li>{@code Authorization: Bearer {accessToken}} (권장)</li>
 *   <li>또는 {@code accessToken: {accessToken}} (네이티브 헤더 이름)</li>
 * </ul>
 * REST 로그인 후 발급된 accessToken과 동일한 값을 사용합니다.
 * (브라우저 쿠키만으로는 STOMP CONNECT에 자동 포함되지 않으므로, 클라이언트 라이브러리에서
 * connectHeaders로 명시적으로 넣어야 합니다.)
 *
 * @see WebSocketConfig#configureClientInboundChannel
 */
@Component
@RequiredArgsConstructor
public class StompJwtChannelInterceptor implements ChannelInterceptor {

    /**
     * CONNECT 이후에도 반드시 로그인 세션이 있어야 하는 STOMP 명령 목록.
     * <ul>
     *   <li>{@link StompCommand#SUBSCRIBE} — /topic/... 구독 (다른 사람 메시지/좌표 수신)</li>
     *   <li>{@link StompCommand#SEND} — /app/... 으로 서버 핸들러 호출 (좌표 전송, 채팅 전송)</li>
     * </ul>
     * CONNECT를 우회한 비정상 클라이언트에 대한 2차 방어선입니다.
     */
    private static final Set<StompCommand> AUTH_REQUIRED_COMMANDS = Set.of(
            StompCommand.SUBSCRIBE,
            StompCommand.SEND
    );

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    /**
     * 클라이언트 → 서버로 들어오는 모든 STOMP 메시지에 대해 호출되는 진입점.
     * <p>
     * {@link ChannelInterceptor}는 메시지가 실제 핸들러({@code @MessageMapping})에 전달되기
     * <em>직전</em>에 실행됩니다. 여기서 예외를 던지면 해당 프레임은 처리되지 않고
     * STOMP ERROR로 클라이언트에 거부됩니다.
     * </p>
     *
     * @param message STOMP 프레임을 감싼 Spring Message (헤더에 command, destination 등 포함)
     * @param channel 내부 메시징 채널 (인바운드 채널)
     * @return 원본 message (인증 성공 시 그대로 다음 단계로 전달)
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        if (accessor.getCommand() == StompCommand.CONNECT) {
            authenticateConnect(accessor);
            return message;
        }

        if (AUTH_REQUIRED_COMMANDS.contains(accessor.getCommand())) {
            requireAuthenticated(accessor);
        }

        return message;
    }

    /**
     * STOMP CONNECT 프레임 처리: JWT를 검증하고 WebSocket 세션에 사용자 Principal을 바인딩.
     * <p>
     * 성공 시 principal에 {@link CustomUserDetails}가 들어가며,
     * {@link StompSessionUserResolver}로 {@link User}를 꺼냅니다.
     * </p>
     *
     * <p><b>실패 시 모두 {@link AccessDeniedException}</b> — 연결 자체가 거부됩니다.</p>
     * <ul>
     *   <li>토큰 없음 / 공백</li>
     *   <li>서명 불일치, 만료 등 JWT 검증 실패</li>
     *   <li>subject(유저 ID) 파싱 불가</li>
     *   <li>DB에 해당 유저 없음 (탈퇴·무효 토큰 등)</li>
     * </ul>
     */
    private void authenticateConnect(StompHeaderAccessor accessor) {
        String token = resolveToken(accessor);
        if (token == null || token.isBlank()) {
            throw new AccessDeniedException("Missing WebSocket token");
        }
        try {
            var claims = jwtUtil.validateToken(token);
            String subject = claims.getSubject();
            if (subject == null || subject.isBlank()) {
                throw new AccessDeniedException("Invalid WebSocket token subject");
            }
            Long userId = Long.valueOf(subject);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AccessDeniedException("User not found for token subject"));
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    new CustomUserDetails(user),
                    null,
                    List.of()
            );
            accessor.setUser(auth);
        } catch (AccessDeniedException ex) {
            throw ex;
        } catch (NumberFormatException ex) {
            throw new AccessDeniedException("Invalid WebSocket token subject", ex);
        } catch (Exception ex) {
            throw new AccessDeniedException("Invalid WebSocket token", ex);
        }
    }

    /**
     * CONNECT 이후 SUBSCRIBE / SEND 에서, 세션에 Principal이 없으면 거부.
     * <p>
     * 정상 클라이언트는 CONNECT에서 이미 setUser 되었으므로 통과합니다.
     * </p>
     */
    private void requireAuthenticated(StompHeaderAccessor accessor) {
        Principal user = accessor.getUser();
        if (user == null) {
            throw new AccessDeniedException("Unauthenticated WebSocket session");
        }
    }

    /**
     * STOMP CONNECT 프레임의 native header에서 JWT 문자열을 추출.
     * <p>
     * STOMP는 HTTP 헤더와 별도로 "native headers" 맵을 가집니다.
     * 프론트 STOMP 클라이언트의 {@code connectHeaders}에 넣은 값이 여기로 전달됩니다.
     * </p>
     * <ol>
     *   <li>{@code Authorization: Bearer xxx} — OAuth2/JWT 관례</li>
     *   <li>{@code accessToken: xxx} — 프로젝트 쿠키 이름과 동일한 키를 헤더로도 허용</li>
     * </ol>
     *
     * @return raw JWT 문자열, 없으면 {@code null}
     */
    private String resolveToken(StompHeaderAccessor accessor) {
        List<String> authHeaders = accessor.getNativeHeader("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String value = authHeaders.getFirst();
            if (value.startsWith("Bearer ")) {
                return value.substring(7);
            }
            return value;
        }
        List<String> tokens = accessor.getNativeHeader("accessToken");
        if (tokens != null && !tokens.isEmpty()) {
            return tokens.getFirst();
        }
        return null;
    }
}
