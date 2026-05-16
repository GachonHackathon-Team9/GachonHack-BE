package com.example.GachonHack.domain.map.dto.res;

public class MapWsResponseDTO {

    public record PositionBroadcastDTO(
            Long userId,
            String nickname,
            Long spaceId,
            Float targetX,
            Float targetY
    ) {}
}
