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
            summary = "층 목록 조회 API",
            description = "층 복도(CORRIDOR) 목록을 반환합니다. 예: AI관 5층 복도(CORRIDOR-5F). "
                    + "3·4·5·PH층은 CORRIDOR 행이 각각 있어야 합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ApiResponse<MapResponseDTO.FloorListResDTO> getFloors();

    @Operation(
            summary = "층별 강의실 목록 API",
            description = "선택한 복도(corridorId) 하위 강의실(LECTURE_ROOM)·스터디룸(STUDY_ROOM) 목록입니다. "
                    + "맵 이동 WebSocket은 corridorId를 spaceId로 사용합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "층 없음")
    })
    ApiResponse<MapResponseDTO.ClassroomListResDTO> getClassroomsByFloor(@PathVariable Long floorId);

    @Operation(
            summary = "강의실 입장 상세 API",
            description = "강의실 입장 팝업용 데이터입니다. 주간 시간표(요일·시간·과목)와 수업별 채팅방 목록을 함께 반환합니다. "
                    + "캐릭터 이동(WebSocket)은 층 floorId 기준 /ws/spaces/{floorId}/move-to, "
                    + "수업 채팅은 chatRoomId 기준 /topic/chat/rooms/{chatRoomId}를 사용합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "강의실 없음")
    })
    ApiResponse<MapResponseDTO.ClassroomDetailResDTO> getClassroomDetail(@PathVariable Long classroomId);

    @Operation(
            summary = "강의실 시간표 조회 API",
            description = "공간(강의실) ID에 해당하는 주간 시간표만 반환합니다. 입장 상세는 GET /classrooms/{classroomId}를 권장합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "시간표 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "공간 없음")
    })
    ApiResponse<MapResponseDTO.TimetableResDTO> getTimetable(@PathVariable Long spaceId);
}
