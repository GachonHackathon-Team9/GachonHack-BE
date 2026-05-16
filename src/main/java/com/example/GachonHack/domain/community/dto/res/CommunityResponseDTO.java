package com.example.GachonHack.domain.community.dto.res;

import com.example.GachonHack.domain.community.enums.BuddyMatchStatus;
import com.example.GachonHack.domain.community.enums.PostType;

import java.time.LocalDateTime;

public class CommunityResponseDTO {

    public record PostSummaryDTO(
            Long id,
            String title,
            String authorNickname,
            String authorRealName,
            PostType type,
            int viewCount,
            int likeCount,
            LocalDateTime createdAt
    ) {}

    public record PostDetailDTO(
            Long id,
            String title,
            String body,
            String authorNickname,
            String authorRealName,
            PostType type,
            int viewCount,
            int likeCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    public record PostCreateResDTO(
            Long id
    ) {}

    public record MentoringRequestDTO(
            Long id,
            Long requesterId,
            String requesterNickname,
            String requesterRealName,
            Long targetId,
            String targetNickname,
            String targetRealName,
            BuddyMatchStatus status,
            LocalDateTime createdAt,
            Long chatSpaceId
    ) {}

    public record MentoringRequestCreateResDTO(
            Long id,
            Long chatSpaceId
    ) {}
}
