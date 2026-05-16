package com.example.GachonHack.domain.map.controller;

import com.example.GachonHack.domain.map.dto.req.MapRequestDTO;
import com.example.GachonHack.domain.map.dto.res.MapWsResponseDTO;
import com.example.GachonHack.domain.map.service.UserPresenceService;
import com.example.GachonHack.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SpaceWsController {

    private final UserPresenceService userPresenceService;

    @MessageMapping("/spaces/{spaceId}/move")
    @SendTo("/topic/spaces/{spaceId}")
    public MapWsResponseDTO.MoveBroadcastDTO move(
            @DestinationVariable Long spaceId,
            MapRequestDTO.MoveReqDTO request,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        User user = resolveUser(headerAccessor);
        return userPresenceService.move(user.getId(), spaceId, request);
    }

    private User resolveUser(SimpMessageHeaderAccessor headerAccessor) {
        Object user = headerAccessor.getUser();
        if (user instanceof org.springframework.security.authentication.UsernamePasswordAuthenticationToken auth) {
            return (User) auth.getPrincipal();
        }
        throw new IllegalStateException("인증된 사용자가 필요합니다.");
    }
}
