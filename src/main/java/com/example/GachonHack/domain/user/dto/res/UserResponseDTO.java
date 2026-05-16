package com.example.GachonHack.domain.user.dto.res;

import java.util.List;

public class UserResponseDTO {

    public record MyPageResDTO(
            String nickname,
            String realName,
            String studentId,
            Short grade,
            List<TitleDTO> titles
    ) {}

    public record TitleDTO(
            Long id,
            String displayText,
            boolean equipped
    ) {}
}
