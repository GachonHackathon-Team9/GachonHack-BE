package com.example.GachonHack.domain.community.dto.req;

import com.example.GachonHack.domain.community.enums.BuddyMatchStatus;
import com.example.GachonHack.domain.community.enums.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CommunityRequestDTO {

    public record PostCreateReqDTO(
            @NotBlank(message = "제목은 필수입니다.")
            @Size(max = 200)
            String title,

            @NotBlank(message = "본문은 필수입니다.")
            String body,

            @NotNull(message = "게시글 유형은 필수입니다.")
            PostType type
    ) {}

    public record CommentCreateReqDTO(
            @NotBlank(message = "댓글 내용은 필수입니다.")
            String body
    ) {}

    public record MentoringRequestCreateReqDTO(
            @NotNull(message = "대상 사용자 ID는 필수입니다.")
            Long targetUserId,

            Long postId
    ) {}

    public record MentoringRequestRespondReqDTO(
            @NotNull(message = "처리 상태는 필수입니다.")
            BuddyMatchStatus status
    ) {}

    public record MentoringMatchFromPostReqDTO(
            @NotNull(message = "대상 사용자 ID는 필수입니다.")
            Long targetUserId
    ) {}
}
