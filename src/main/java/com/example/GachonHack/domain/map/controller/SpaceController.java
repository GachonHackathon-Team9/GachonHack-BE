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
    @GetMapping("/{spaceId}/timetable")
    public ApiResponse<MapResponseDTO.TimetableResDTO> getTimetable(@PathVariable Long spaceId) {
        return ApiResponse.onSuccess(MapSuccessCode.TIMETABLE_SUCCESS, spaceService.getTimetable(spaceId));
    }
}
