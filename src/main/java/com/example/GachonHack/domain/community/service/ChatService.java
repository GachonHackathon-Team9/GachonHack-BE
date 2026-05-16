package com.example.GachonHack.domain.community.service;

import com.example.GachonHack.domain.community.dto.req.ChatRequestDTO;
import com.example.GachonHack.domain.community.dto.res.ChatResponseDTO;
import com.example.GachonHack.domain.community.entity.ChatMessage;
import com.example.GachonHack.domain.community.entity.ChatRoom;
import com.example.GachonHack.domain.community.exception.CommunityException;
import com.example.GachonHack.domain.community.exception.code.CommunityErrorCode;
import com.example.GachonHack.domain.community.repository.ChatMessageRepository;
import com.example.GachonHack.domain.community.repository.ChatRoomRepository;
import com.example.GachonHack.domain.map.entity.Space;
import com.example.GachonHack.domain.map.exception.MapException;
import com.example.GachonHack.domain.map.exception.code.MapErrorCode;
import com.example.GachonHack.domain.map.repository.SpaceRepository;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatResponseDTO.MessageBroadcastDTO sendMessage(
            Long userId,
            Long spaceId,
            ChatRequestDTO.SendMessageReqDTO request
    ) {
        if (request.body() == null || request.body().isBlank()) {
            throw new CommunityException(CommunityErrorCode.CHAT_MESSAGE_EMPTY);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.USER_NOT_FOUND));
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new MapException(MapErrorCode.SPACE_NOT_FOUND));
        ChatRoom room = chatRoomRepository.findFirstBySpaceAndActiveTrueOrderByCreatedAtDesc(space)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.CHAT_ROOM_NOT_FOUND));

        ChatMessage message = chatMessageRepository.save(ChatMessage.builder()
                .room(room)
                .user(user)
                .body(request.body().trim())
                .build());

        return new ChatResponseDTO.MessageBroadcastDTO(
                message.getId(),
                room.getId(),
                user.getId(),
                user.getNickname(),
                message.getBody(),
                message.getCreatedAt()
        );
    }
}
