package com.example.GachonHack.domain.community.controller;

import com.example.GachonHack.domain.community.dto.req.CommunityRequestDTO;
import com.example.GachonHack.domain.community.dto.res.CommunityResponseDTO;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Mentoring", description = "짝선짝후 매칭 관련 API")
public interface MentoringControllerDocs {

    @Operation(summary = "짝선짝후 매칭 신청 API", description = "대상 사용자에게 매칭을 신청합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "신청 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "중복 신청 등")
    })
    ApiResponse<CommunityResponseDTO.MentoringRequestCreateResDTO> createRequest(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CommunityRequestDTO.MentoringRequestCreateReqDTO request
    );

    @Operation(summary = "게시글 기반 매칭 신청 API", description = "짝선짝후 게시글 작성자(새내기)가 선배에게 매칭을 신청합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "신청 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
    ApiResponse<CommunityResponseDTO.MentoringRequestCreateResDTO> confirmMatchFromPost(
            @AuthenticationPrincipal User user,
            @PathVariable Long postId,
            @Valid @RequestBody CommunityRequestDTO.MentoringMatchFromPostReqDTO request
    );

    @Operation(summary = "받은 매칭 신청 목록 API", description = "나에게 온 대기(PENDING) 중인 매칭 신청 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ApiResponse<List<CommunityResponseDTO.MentoringRequestDTO>> getIncomingRequests(
            @AuthenticationPrincipal User user
    );

    @Operation(summary = "매칭 신청 승인/거절 API", description = "대상자가 매칭 신청을 승인(ACCEPTED) 또는 거절(REJECTED)합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "처리 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "신청 없음")
    })
    ApiResponse<CommunityResponseDTO.MentoringRequestDTO> respondToRequest(
            @AuthenticationPrincipal User user,
            @PathVariable Long reqId,
            @Valid @RequestBody CommunityRequestDTO.MentoringRequestRespondReqDTO request
    );

    @Operation(summary = "내 짝꿍 조회 API", description = "승인된(ACCEPTED) 매칭 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ApiResponse<List<CommunityResponseDTO.MentoringRequestDTO>> getMyMatches(
            @AuthenticationPrincipal User user
    );
}
