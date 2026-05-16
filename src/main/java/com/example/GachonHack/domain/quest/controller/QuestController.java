package com.example.GachonHack.domain.quest.controller;

import com.example.GachonHack.domain.quest.dto.req.QuestRequestDTO;
import com.example.GachonHack.domain.quest.dto.res.QuestResponseDTO;
import com.example.GachonHack.domain.quest.exception.code.QuestSuccessCode;
import com.example.GachonHack.domain.quest.service.QuestService;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/quests", "/quests"})
public class QuestController implements QuestControllerDocs {

    private final QuestService questService;

    @Override
    @GetMapping("/daily")
    public ApiResponse<QuestResponseDTO.DailyQuestListResDTO> getDailyQuests(
            @AuthenticationPrincipal(expression = "user") User user
    ) {
        return ApiResponse.onSuccess(
                QuestSuccessCode.DAILY_LIST_SUCCESS,
                questService.getDailyQuests(user.getId())
        );
    }

    @Override
    @PostMapping("/{questId}/verify")
    public ApiResponse<QuestResponseDTO.QuestVerifyResDTO> verifyQuest(
            @AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long questId
    ) {
        return ApiResponse.onSuccess(
                QuestSuccessCode.VERIFY_SUCCESS,
                questService.verifyQuest(user.getId(), questId)
        );
    }

    @Override
    @PostMapping("/{questId}/reward")
    public ApiResponse<QuestResponseDTO.QuestRewardResDTO> rewardQuest(
            @AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long questId,
            @Valid @RequestBody QuestRequestDTO.QuestRewardReqDTO request
    ) {
        return ApiResponse.onSuccess(
                QuestSuccessCode.REWARD_SUCCESS,
                questService.rewardQuest(user.getId(), questId, request)
        );
    }
}
