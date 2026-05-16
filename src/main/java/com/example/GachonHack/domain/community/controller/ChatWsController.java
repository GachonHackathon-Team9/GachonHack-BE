package com.example.GachonHack.domain.community.controller;

import com.example.GachonHack.domain.community.dto.req.ChatRequestDTO;
import com.example.GachonHack.domain.community.dto.res.ChatResponseDTO;
import com.example.GachonHack.domain.community.service.ChatService;
import com.example.GachonHack.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWsController {

    private final ChatService chatService;

    @MessageMapping("/chat/{spaceId}/send")
    @SendTo("/topic/chat/{spaceId}")
    public ChatResponseDTO.MessageBroadcastDTO send(
            @DestinationVariable Long spaceId,
            ChatRequestDTO.SendMessageReqDTO request,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        User user = resolveUser(headerAccessor);
        return chatService.sendMessage(user.getId(), spaceId, request);
    }

    private User resolveUser(SimpMessageHeaderAccessor headerAccessor) {
        Object user = headerAccessor.getUser();
        if (user instanceof org.springframework.security.authentication.UsernamePasswordAuthenticationToken auth) {
            return (User) auth.getPrincipal();
        }
        throw new IllegalStateException("인증된 사용자가 필요합니다.");
    }
}
