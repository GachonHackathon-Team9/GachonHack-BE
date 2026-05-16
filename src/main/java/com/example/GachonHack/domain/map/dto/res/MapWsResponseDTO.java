package com.example.GachonHack.domain.map.dto.res;

public class MapWsResponseDTO {

    public record MoveBroadcastDTO(
            Long userId,
            String nickname,
            Long spaceId,
            Float posX,
            Float posY
    ) {}
}
