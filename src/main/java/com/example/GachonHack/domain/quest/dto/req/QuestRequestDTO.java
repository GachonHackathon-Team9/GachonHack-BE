package com.example.GachonHack.domain.quest.dto.req;

import com.example.GachonHack.domain.quest.enums.QuestRewardAction;
import jakarta.validation.constraints.NotNull;

public class QuestRequestDTO {

    public record QuestRewardReqDTO(
            @NotNull Long userId,
            @NotNull QuestRewardAction action,
            String reviewerNote
    ) {}
}
