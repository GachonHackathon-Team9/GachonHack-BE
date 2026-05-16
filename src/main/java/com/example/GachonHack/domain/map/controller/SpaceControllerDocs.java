package com.example.GachonHack.domain.map.controller;

import com.example.GachonHack.domain.map.dto.res.MapResponseDTO;
import com.example.GachonHack.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Space", description = "캠퍼스 맵 · 공간 관련 API")
public interface SpaceControllerDocs {

    @Operation(
            summary = "강의실 시간표 조회 API",
            description = "공간(강의실) ID에 해당하는 주간 시간표를 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "시간표 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "공간 없음")
    })
    ApiResponse<MapResponseDTO.TimetableResDTO> getTimetable(@PathVariable Long spaceId);
}
