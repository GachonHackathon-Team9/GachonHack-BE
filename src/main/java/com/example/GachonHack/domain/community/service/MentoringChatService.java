package com.example.GachonHack.domain.community.service;

import com.example.GachonHack.domain.community.entity.BuddyMatchRequest;
import com.example.GachonHack.domain.community.entity.ChatRoom;
import com.example.GachonHack.domain.community.enums.ChatRoomKind;
import com.example.GachonHack.domain.community.repository.ChatRoomRepository;
import com.example.GachonHack.domain.map.entity.Space;
import com.example.GachonHack.domain.map.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MentoringChatService {

    private static final String MENTORING_SPACE_TYPE = "MENTORING";

    private final ChatRoomRepository chatRoomRepository;
    private final SpaceRepository spaceRepository;

    @Transactional
    public ChatRoom createRoomForMatch(BuddyMatchRequest match) {
        return chatRoomRepository.findByBuddyMatchAndActiveTrue(match)
                .orElseGet(() -> {
                    try {
                        return saveMentoringRoom(match);
                    } catch (DataIntegrityViolationException ex) {
                        return chatRoomRepository.findByBuddyMatchAndActiveTrue(match)
                                .orElseThrow(() -> ex);
                    }
                });
    }

    private ChatRoom saveMentoringRoom(BuddyMatchRequest match) {
        Space space = spaceRepository.save(Space.builder()
                .type(MENTORING_SPACE_TYPE)
                .code("mentoring-" + match.getId())
                .name("짝선짝후 " + match.getId())
                .sortOrder(0)
                .build());
        return chatRoomRepository.save(ChatRoom.builder()
                .space(space)
                .kind(ChatRoomKind.MENTORING)
                .active(true)
                .buddyMatch(match)
                .build());
    }

    public static boolean isMentoringSpace(Space space) {
        return MENTORING_SPACE_TYPE.equals(space.getType());
    }
}
