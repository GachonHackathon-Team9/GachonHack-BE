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

@Validated
@Controller
@RequiredArgsConstructor
public class SpaceWsController {

    private final UserPresenceService userPresenceService;

    /**
     * 사용자가 맵에서 클릭한 목표 좌표로 캐릭터 위치를 갱신하고, 같은 공간 구독자에게 브로드캐스트합니다.
     * 프론트는 클릭 지점의 월드/타일 좌표를 targetX, targetY로 전송하면 됩니다.
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

    private User resolveUser(SimpMessageHeaderAccessor headerAccessor) {
        Object user = headerAccessor.getUser();
        if (user instanceof org.springframework.security.authentication.UsernamePasswordAuthenticationToken auth) {
            return (User) auth.getPrincipal();
        }
        throw new IllegalStateException("인증된 사용자가 필요합니다.");
    }
}
