package com.example.GachonHack.domain.user.dto.res;

import java.util.List;

public class UserResponseDTO {

    public record MyPageResDTO(
            String nickname,
            String realName,
            String studentId,
            Short grade,
            Integer pointBalance,
            List<BadgeDTO> badges,
            List<TitleDTO> titles
    ) {}

    public record BadgeDTO(
            Long id,
            String displayName,
            String iconUrl,
            boolean equipped
    ) {}

    public record TitleDTO(
            Long id,
            String displayText,
            boolean equipped
    ) {}
}
