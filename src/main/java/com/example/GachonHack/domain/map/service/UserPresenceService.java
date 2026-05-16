package com.example.GachonHack.domain.map.service;

import com.example.GachonHack.domain.community.exception.CommunityException;
import com.example.GachonHack.domain.community.exception.code.CommunityErrorCode;
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
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
        if (request.targetX() == null || request.targetY() == null) {
            throw new MapException(MapErrorCode.INVALID_COORDINATES);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.USER_NOT_FOUND));
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new MapException(MapErrorCode.SPACE_NOT_FOUND));

        float targetX = request.targetX();
        float targetY = request.targetY();

        upsertPresence(user, space, targetX, targetY);

        return new MapWsResponseDTO.PositionBroadcastDTO(
                user.getId(),
                user.getNickname(),
                space.getId(),
                targetX,
                targetY
        );
    }

    private void upsertPresence(User user, Space space, float targetX, float targetY) {
        try {
            savePresence(user, space, targetX, targetY);
        } catch (DataIntegrityViolationException ex) {
            savePresence(user, space, targetX, targetY);
        }
    }

    private void savePresence(User user, Space space, float targetX, float targetY) {
        UserPresence presence = userPresenceRepository.findByUser(user)
                .orElseGet(() -> UserPresence.builder().user(user).space(space).build());
        presence.moveTo(space, targetX, targetY);
        userPresenceRepository.save(presence);
    }
}
