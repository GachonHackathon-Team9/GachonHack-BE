package com.example.GachonHack.domain.quest.controller;

import com.example.GachonHack.domain.quest.dto.req.QuestRequestDTO;
import com.example.GachonHack.domain.quest.dto.res.QuestResponseDTO;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Quest", description = "퀘스트 관련 API")
public interface QuestControllerDocs {

    @Operation(summary = "일일 퀘스트 목록 API", description = "활성화된 일일 퀘스트와 내 제출 상태를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ApiResponse<QuestResponseDTO.DailyQuestListResDTO> getDailyQuests(
            @AuthenticationPrincipal User user
    );

    @Operation(summary = "퀘스트 완료 신청 API", description = "완료 버튼을 눌러 검수 대기(PENDING) 상태로 제출합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "신청 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "중복 신청 등")
    })
    ApiResponse<QuestResponseDTO.QuestVerifyResDTO> verifyQuest(
            @AuthenticationPrincipal User user,
            @PathVariable Long questId
    );

    @Operation(summary = "퀘스트 검수·포인트 지급 API", description = "관리자가 제출을 승인·거절합니다. 승인 시 포인트가 지급됩니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "처리 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    ApiResponse<QuestResponseDTO.QuestRewardResDTO> rewardQuest(
            @AuthenticationPrincipal User user,
            @PathVariable Long questId,
            @Valid @RequestBody QuestRequestDTO.QuestRewardReqDTO request
    );
}
