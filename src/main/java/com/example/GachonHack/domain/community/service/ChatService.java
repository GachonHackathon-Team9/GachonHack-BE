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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final Pageable FIRST_ROOM = PageRequest.of(0, 1);

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatResponseDTO.MessageBroadcastDTO sendMessageByChatRoomId(
            Long userId,
            Long chatRoomId,
            ChatRequestDTO.SendMessageReqDTO request
    ) {
        validateMessageBody(request);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.USER_NOT_FOUND));
        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.CHAT_ROOM_NOT_FOUND));
        if (!room.isActive()) {
            throw new CommunityException(CommunityErrorCode.CHAT_ROOM_NOT_FOUND);
        }
        validateChatAccess(room, userId);
        ChatMessage message = chatMessageRepository.save(ChatMessage.builder()
                .room(room)
                .user(user)
                .body(request.body().trim())
                .build());
        return toBroadcast(message, room, user);
    }

    @Transactional
    public ChatResponseDTO.MessageBroadcastDTO sendMessage(
            Long userId,
            Long spaceId,
            ChatRequestDTO.SendMessageReqDTO request
    ) {
        validateMessageBody(request);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.USER_NOT_FOUND));
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new MapException(MapErrorCode.SPACE_NOT_FOUND));
        ChatRoom room = resolveRoom(space);
        validateChatAccess(room, userId);
        ChatMessage message = chatMessageRepository.save(ChatMessage.builder()
                .room(room)
                .user(user)
                .body(request.body().trim())
                .build());
        return toBroadcast(message, room, user);
    }

    private ChatRoom resolveRoom(Space space) {
        List<ChatRoom> rooms = MentoringChatService.isMentoringSpace(space)
                ? chatRoomRepository.findActiveBySpaceWithParticipants(space, FIRST_ROOM)
                : chatRoomRepository.findActivePublicSpaceRooms(space, FIRST_ROOM);
        return rooms.stream()
                .findFirst()
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.CHAT_ROOM_NOT_FOUND));
    }

    private void validateMessageBody(ChatRequestDTO.SendMessageReqDTO request) {
        if (request.body() == null || request.body().isBlank()) {
            throw new CommunityException(CommunityErrorCode.CHAT_MESSAGE_EMPTY);
        }
    }

    private void validateChatAccess(ChatRoom room, Long userId) {
        if (room.getBuddyMatch() == null) {
            return;
        }
        var match = room.getBuddyMatch();
        boolean participant = match.getRequester().getId().equals(userId)
                || match.getTarget().getId().equals(userId);
        if (!participant) {
            throw new CommunityException(CommunityErrorCode.MATCH_REQUEST_FORBIDDEN);
        }
    }

    private ChatResponseDTO.MessageBroadcastDTO toBroadcast(ChatMessage message, ChatRoom room, User user) {
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
