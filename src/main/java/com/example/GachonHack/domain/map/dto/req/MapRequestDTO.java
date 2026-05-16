package com.example.GachonHack.domain.map.dto.req;

import jakarta.validation.constraints.NotNull;

public class MapRequestDTO {

    /**
     * 맵에서 사용자가 클릭한 목표 지점 좌표.
     * 방향키/조이스틱 델타가 아니라, 이동 완료(또는 클릭) 시점의 절대 좌표입니다.
     */
    public record MoveToPositionReqDTO(
            @NotNull(message = "목표 X 좌표는 필수입니다.")
            Float targetX,

            @NotNull(message = "목표 Y 좌표는 필수입니다.")
            Float targetY
    ) {}
}
