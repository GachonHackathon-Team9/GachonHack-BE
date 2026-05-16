package com.example.GachonHack.domain.community.dto.res;

import com.example.GachonHack.domain.community.enums.BuddyMatchStatus;
import com.example.GachonHack.domain.community.enums.PostType;

import java.time.LocalDateTime;
import java.util.List;

public class CommunityResponseDTO {

    public record PostSummaryDTO(
            Long id,
            String title,
            String authorNickname,
            String authorRealName,
            PostType type,
            int viewCount,
            int likeCount,
            boolean isLiked,
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
            boolean isLiked,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            List<CommentDTO> comments
    ) {}

    public record CommentDTO(
            Long id,
            String authorNickname,
            String authorRealName,
            String body,
            LocalDateTime createdAt
    ) {}

    public record PostLikeResDTO(
            Long postId,
            int likeCount
    ) {}

    public record PostCreateResDTO(
            Long id
    ) {}

    public record CommentCreateResDTO(
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
