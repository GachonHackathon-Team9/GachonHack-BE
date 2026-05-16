package com.example.GachonHack.domain.quest.service;

import com.example.GachonHack.domain.point.entity.PointLedger;
import com.example.GachonHack.domain.point.enums.PointReason;
import com.example.GachonHack.domain.point.repository.PointLedgerRepository;
import com.example.GachonHack.domain.quest.dto.req.QuestRequestDTO;
import com.example.GachonHack.domain.quest.dto.res.QuestResponseDTO;
import com.example.GachonHack.domain.quest.entity.Quest;
import com.example.GachonHack.domain.quest.entity.QuestSubmission;
import com.example.GachonHack.domain.quest.enums.QuestRewardAction;
import com.example.GachonHack.domain.quest.enums.SubmissionStatus;
import com.example.GachonHack.domain.quest.exception.QuestException;
import com.example.GachonHack.domain.quest.exception.code.QuestErrorCode;
import com.example.GachonHack.domain.quest.repository.QuestRepository;
import com.example.GachonHack.domain.quest.repository.QuestSubmissionRepository;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.domain.user.enums.Role;
import com.example.GachonHack.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestService {

    private static final String QUEST_SUBMISSION_REF_TYPE = "QUEST_SUBMISSION";
    private static final List<SubmissionStatus> BLOCKING_STATUSES = List.of(
            SubmissionStatus.PENDING,
            SubmissionStatus.APPROVED
    );

    private final QuestRepository questRepository;
    private final QuestSubmissionRepository questSubmissionRepository;
    private final UserRepository userRepository;
    private final PointLedgerRepository pointLedgerRepository;

    @Transactional(readOnly = true)
    public QuestResponseDTO.DailyQuestListResDTO getDailyQuests(Long userId) {
        User user = findUser(userId);
        List<QuestResponseDTO.DailyQuestItemDTO> items = questRepository.findByDailyTrueAndActiveTrueOrderByIdAsc()
                .stream()
                .map(quest -> toDailyQuestItem(quest, user))
                .toList();
        return new QuestResponseDTO.DailyQuestListResDTO(items);
    }

    @Transactional
    public QuestResponseDTO.QuestVerifyResDTO verifyQuest(Long userId, Long questId) {
        User user = findUser(userId);
        Quest quest = findActiveDailyQuest(questId);
        if (questSubmissionRepository.existsByQuestAndUserAndStatusIn(quest, user, BLOCKING_STATUSES)) {
            throw new QuestException(QuestErrorCode.SUBMISSION_ALREADY_EXISTS);
        }
        LocalDateTime now = LocalDateTime.now();
        QuestSubmission saved = questSubmissionRepository.save(QuestSubmission.builder()
                .quest(quest)
                .user(user)
                .status(SubmissionStatus.PENDING)
                .submittedAt(now)
                .build());
        return new QuestResponseDTO.QuestVerifyResDTO(
                saved.getId(),
                quest.getId(),
                saved.getStatus(),
                saved.getSubmittedAt()
        );
    }

    @Transactional
    public QuestResponseDTO.QuestRewardResDTO rewardQuest(
            Long adminUserId,
            Long questId,
            QuestRequestDTO.QuestRewardReqDTO request
    ) {
        User admin = findUser(adminUserId);
        if (admin.getRole() != Role.ADMIN) {
            throw new QuestException(QuestErrorCode.ADMIN_ONLY);
        }
        Quest quest = findQuest(questId);
        if (!quest.isDaily() || !quest.isActive()) {
            throw new QuestException(QuestErrorCode.QUEST_NOT_ACTIVE);
        }
        User targetUser = findUser(request.userId());
        QuestSubmission submission = questSubmissionRepository
                .findByQuestAndUserAndStatus(quest, targetUser, SubmissionStatus.PENDING)
                .orElseThrow(() -> new QuestException(QuestErrorCode.SUBMISSION_NOT_FOUND));
        if (request.action() == QuestRewardAction.REJECT) {
            submission.review(SubmissionStatus.REJECTED, request.reviewerNote());
            return new QuestResponseDTO.QuestRewardResDTO(
                    submission.getId(),
                    quest.getId(),
                    targetUser.getId(),
                    submission.getStatus(),
                    0,
                    targetUser.getPointBalance()
            );
        }
        if (request.action() != QuestRewardAction.APPROVE) {
            throw new QuestException(QuestErrorCode.INVALID_REWARD_ACTION);
        }
        if (pointLedgerRepository.existsByUserAndReasonAndRefTypeAndRefId(
                targetUser,
                PointReason.QUEST_REWARD,
                QUEST_SUBMISSION_REF_TYPE,
                submission.getId()
        )) {
            throw new QuestException(QuestErrorCode.REWARD_ALREADY_GRANTED);
        }
        submission.review(SubmissionStatus.APPROVED, request.reviewerNote());
        int rewardPoints = quest.getRewardPoints();
        targetUser.adjustPoint(rewardPoints);
        int balanceAfter = targetUser.getPointBalance();
        pointLedgerRepository.save(PointLedger.builder()
                .user(targetUser)
                .amount(rewardPoints)
                .reason(PointReason.QUEST_REWARD)
                .refType(QUEST_SUBMISSION_REF_TYPE)
                .refId(submission.getId())
                .balanceAfter(balanceAfter)
                .build());
        return new QuestResponseDTO.QuestRewardResDTO(
                submission.getId(),
                quest.getId(),
                targetUser.getId(),
                submission.getStatus(),
                rewardPoints,
                balanceAfter
        );
    }

    private QuestResponseDTO.DailyQuestItemDTO toDailyQuestItem(Quest quest, User user) {
        SubmissionStatus status = questSubmissionRepository
                .findFirstByQuestAndUserOrderBySubmittedAtDesc(quest, user)
                .map(QuestSubmission::getStatus)
                .orElse(null);
        Long spaceId = quest.getSpace() != null ? quest.getSpace().getId() : null;
        String spaceName = quest.getSpace() != null ? quest.getSpace().getName() : null;
        return new QuestResponseDTO.DailyQuestItemDTO(
                quest.getId(),
                quest.getCode(),
                quest.getTitle(),
                quest.getDescription(),
                quest.getQuestType(),
                quest.getRewardPoints(),
                spaceId,
                spaceName,
                quest.getRequiredMinutes(),
                status
        );
    }

    private Quest findActiveDailyQuest(Long questId) {
        Quest quest = findQuest(questId);
        if (!quest.isDaily() || !quest.isActive()) {
            throw new QuestException(QuestErrorCode.QUEST_NOT_ACTIVE);
        }
        return quest;
    }

    private Quest findQuest(Long questId) {
        return questRepository.findById(questId)
                .orElseThrow(() -> new QuestException(QuestErrorCode.QUEST_NOT_FOUND));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new QuestException(QuestErrorCode.USER_NOT_FOUND));
    }
}
