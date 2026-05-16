package com.example.GachonHack.domain.user.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserRequestDTO {
    public record OnboardingReqDTO(
            @NotBlank(message = "실명은 필수 입력 사항입니다.")
            @Size(max = 10)
            String real_name,

            @NotBlank(message = "학번은 필수 입력 사항입니다.")
            @Size(max = 9)
            String student_id,

            @NotNull(message = "학년은 필수 입력 사항입니다.")
            Short grade
    ) {}

    public record EquipmentUpdateReqDTO(
            @NotNull(message = "장착할 칭호 ID는 필수입니다.")
            Long userTitleId
    ) {}
}
