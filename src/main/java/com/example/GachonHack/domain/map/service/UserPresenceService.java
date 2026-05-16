package com.example.GachonHack.domain.map.service;

import com.example.GachonHack.domain.map.dto.req.MapRequestDTO;
import com.example.GachonHack.domain.map.dto.res.MapWsResponseDTO;
import com.example.GachonHack.domain.map.entity.Space;
import com.example.GachonHack.domain.map.entity.UserPresence;
import com.example.GachonHack.domain.map.exception.MapException;
import com.example.GachonHack.domain.map.exception.code.MapErrorCode;
import com.example.GachonHack.domain.map.repository.SpaceRepository;
import com.example.GachonHack.domain.map.repository.UserPresenceRepository;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.domain.user.repository.UserRepository;
import com.example.GachonHack.domain.community.exception.CommunityException;
import com.example.GachonHack.domain.community.exception.code.CommunityErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPresenceService {

    private final UserPresenceRepository userPresenceRepository;
    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;

    @Transactional
    public MapWsResponseDTO.PositionBroadcastDTO moveToPosition(
            Long userId,
            Long spaceId,
            MapRequestDTO.MoveToPositionReqDTO request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.USER_NOT_FOUND));
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new MapException(MapErrorCode.SPACE_NOT_FOUND));

        float targetX = request.targetX();
        float targetY = request.targetY();

        UserPresence presence = userPresenceRepository.findByUser(user)
                .orElseGet(() -> UserPresence.builder().user(user).space(space).build());
        presence.moveTo(space, targetX, targetY);
        userPresenceRepository.save(presence);

        return new MapWsResponseDTO.PositionBroadcastDTO(
                user.getId(),
                user.getNickname(),
                space.getId(),
                targetX,
                targetY
        );
    }
}
