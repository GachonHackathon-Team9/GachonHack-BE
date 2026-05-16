package com.example.GachonHack.domain.community.controller;

import com.example.GachonHack.domain.community.dto.req.CommunityRequestDTO;
import com.example.GachonHack.domain.community.dto.res.CommunityResponseDTO;
import com.example.GachonHack.domain.community.exception.code.CommunitySuccessCode;
import com.example.GachonHack.domain.community.service.MentoringService;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mentoring")
public class MentoringController implements MentoringControllerDocs {

    private final MentoringService mentoringService;

    @Override
    @PostMapping("/requests")
    public ApiResponse<CommunityResponseDTO.MentoringRequestCreateResDTO> createRequest(
            @AuthenticationPrincipal(expression = "user") User user,
            @Valid @RequestBody CommunityRequestDTO.MentoringRequestCreateReqDTO request
    ) {
        return ApiResponse.onSuccess(
                CommunitySuccessCode.MENTORING_REQUEST_CREATE_SUCCESS,
                mentoringService.createRequest(user.getId(), request)
        );
    }

    @Override
    @PostMapping("/{postId}/match")
    public ApiResponse<CommunityResponseDTO.MentoringRequestCreateResDTO> confirmMatchFromPost(
            @AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long postId,
            @Valid @RequestBody CommunityRequestDTO.MentoringMatchFromPostReqDTO request
    ) {
        return ApiResponse.onSuccess(
                CommunitySuccessCode.MENTORING_MATCH_CONFIRM_SUCCESS,
                mentoringService.confirmMatchFromPost(user.getId(), postId, request)
        );
    }

    @Override
    @GetMapping("/requests")
    public ApiResponse<List<CommunityResponseDTO.MentoringRequestDTO>> getIncomingRequests(
            @AuthenticationPrincipal(expression = "user") User user
    ) {
        return ApiResponse.onSuccess(
                CommunitySuccessCode.MENTORING_REQUEST_LIST_SUCCESS,
                mentoringService.getIncomingRequests(user.getId())
        );
    }

    @Override
    @PatchMapping("/requests/{reqId}")
    public ApiResponse<CommunityResponseDTO.MentoringRequestDTO> respondToRequest(
            @AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long reqId,
            @Valid @RequestBody CommunityRequestDTO.MentoringRequestRespondReqDTO request
    ) {
        return ApiResponse.onSuccess(
                CommunitySuccessCode.MENTORING_REQUEST_UPDATE_SUCCESS,
                mentoringService.respondToRequest(user.getId(), reqId, request)
        );
    }

    @Override
    @GetMapping("/my-matches")
    public ApiResponse<List<CommunityResponseDTO.MentoringRequestDTO>> getMyMatches(
            @AuthenticationPrincipal(expression = "user") User user
    ) {
        return ApiResponse.onSuccess(
                CommunitySuccessCode.MENTORING_MATCH_LIST_SUCCESS,
                mentoringService.getMyMatches(user.getId())
        );
    }
}
