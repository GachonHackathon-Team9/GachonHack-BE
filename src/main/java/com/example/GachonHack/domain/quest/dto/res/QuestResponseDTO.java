package com.example.GachonHack.domain.quest.dto.res;

import com.example.GachonHack.domain.quest.enums.QuestType;
import com.example.GachonHack.domain.quest.enums.SubmissionStatus;

import java.time.LocalDateTime;
import java.util.List;

public class QuestResponseDTO {

    public record DailyQuestItemDTO(
            Long questId,
            String code,
            String title,
            String description,
            QuestType questType,
            Integer rewardPoints,
            Long spaceId,
            String spaceName,
            Integer requiredMinutes,
            SubmissionStatus submissionStatus
    ) {}

    public record DailyQuestListResDTO(
            List<DailyQuestItemDTO> quests
    ) {}

    public record QuestVerifyResDTO(
            Long submissionId,
            Long questId,
            SubmissionStatus status,
            LocalDateTime submittedAt
    ) {}

    public record QuestRewardResDTO(
            Long submissionId,
            Long questId,
            Long userId,
            SubmissionStatus status,
            Integer rewardPoints,
            Integer balanceAfter
    ) {}
}
