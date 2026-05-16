package com.example.GachonHack.global.config.websocket;

import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.domain.user.repository.UserRepository;
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

@Component
@RequiredArgsConstructor
public class StompJwtChannelInterceptor implements ChannelInterceptor {

    private static final Set<StompCommand> AUTH_REQUIRED_COMMANDS = Set.of(
            StompCommand.SUBSCRIBE,
            StompCommand.SEND
    );

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

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
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(user, null, List.of());
            accessor.setUser(auth);
        } catch (AccessDeniedException ex) {
            throw ex;
        } catch (NumberFormatException ex) {
            throw new AccessDeniedException("Invalid WebSocket token subject", ex);
        } catch (Exception ex) {
            throw new AccessDeniedException("Invalid WebSocket token", ex);
        }
    }

    private void requireAuthenticated(StompHeaderAccessor accessor) {
        Principal user = accessor.getUser();
        if (user == null) {
            throw new AccessDeniedException("Unauthenticated WebSocket session");
        }
    }

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
