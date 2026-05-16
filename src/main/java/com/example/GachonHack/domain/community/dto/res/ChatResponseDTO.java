package com.example.GachonHack.domain.community.dto.res;

import java.time.LocalDateTime;

public class ChatResponseDTO {

    public record MessageBroadcastDTO(
            Long messageId,
            Long roomId,
            Long userId,
            String nickname,
            String body,
            LocalDateTime createdAt
    ) {}
}
