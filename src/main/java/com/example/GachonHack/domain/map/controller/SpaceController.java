package com.example.GachonHack.domain.map.controller;

import com.example.GachonHack.domain.map.dto.res.MapResponseDTO;
import com.example.GachonHack.domain.map.exception.code.MapSuccessCode;
import com.example.GachonHack.domain.map.service.SpaceService;
import com.example.GachonHack.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/spaces", "/spaces"})
public class SpaceController implements SpaceControllerDocs {

    private final SpaceService spaceService;

    @Override
    @GetMapping("/floors")
    public ApiResponse<MapResponseDTO.FloorListResDTO> getFloors() {
        return ApiResponse.onSuccess(MapSuccessCode.FLOOR_LIST_SUCCESS, spaceService.getFloors());
    }

    @Override
    @GetMapping("/floors/{floorId}/classrooms")
    public ApiResponse<MapResponseDTO.ClassroomListResDTO> getClassroomsByFloor(@PathVariable Long floorId) {
        return ApiResponse.onSuccess(
                MapSuccessCode.CLASSROOM_LIST_SUCCESS,
                spaceService.getClassroomsByFloor(floorId)
        );
    }

    @Override
    @GetMapping("/classrooms/{classroomId}")
    public ApiResponse<MapResponseDTO.ClassroomDetailResDTO> getClassroomDetail(@PathVariable Long classroomId) {
        return ApiResponse.onSuccess(
                MapSuccessCode.CLASSROOM_DETAIL_SUCCESS,
                spaceService.getClassroomDetail(classroomId)
        );
    }

    @Override
    @GetMapping("/{spaceId}/timetable")
    public ApiResponse<MapResponseDTO.TimetableResDTO> getTimetable(@PathVariable Long spaceId) {
        return ApiResponse.onSuccess(MapSuccessCode.TIMETABLE_SUCCESS, spaceService.getTimetable(spaceId));
    }
}
