package com.example.GachonHack.domain.quest.service;

import com.example.GachonHack.domain.community.entity.BuddyMatchRequest;
import com.example.GachonHack.domain.community.enums.BuddyMatchStatus;
import com.example.GachonHack.domain.community.repository.BuddyMatchRequestRepository;
import com.example.GachonHack.domain.point.entity.PointLedger;
import com.example.GachonHack.domain.point.enums.PointReason;
import com.example.GachonHack.domain.point.repository.PointLedgerRepository;
import com.example.GachonHack.domain.quest.dto.req.QuestRequestDTO;
import com.example.GachonHack.domain.quest.dto.res.QuestResponseDTO;
import com.example.GachonHack.domain.quest.entity.BuddyMatchQuest;
import com.example.GachonHack.domain.quest.entity.Quest;
import com.example.GachonHack.domain.quest.entity.UserQuest;
import com.example.GachonHack.domain.quest.enums.QuestRewardAction;
import com.example.GachonHack.domain.quest.enums.QuestType;
import com.example.GachonHack.domain.quest.enums.UserQuestStatus;
import com.example.GachonHack.domain.quest.exception.QuestException;
import com.example.GachonHack.domain.quest.exception.code.QuestErrorCode;
import com.example.GachonHack.domain.quest.repository.BuddyMatchQuestRepository;
import com.example.GachonHack.domain.quest.repository.QuestRepository;
import com.example.GachonHack.domain.quest.repository.UserQuestRepository;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.domain.user.enums.Role;
import com.example.GachonHack.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestService {

    private static final String USER_QUEST_REF_TYPE = "USER_QUEST";
    private static final String BUDDY_MATCH_QUEST_REF_TYPE = "BUDDY_MATCH_QUEST";
    private static final List<UserQuestStatus> BLOCKING_STATUSES = List.of(
            UserQuestStatus.PENDING,
            UserQuestStatus.APPROVED
    );

    private final QuestRepository questRepository;
    private final UserQuestRepository userQuestRepository;
    private final BuddyMatchQuestRepository buddyMatchQuestRepository;
    private final BuddyMatchRequestRepository buddyMatchRequestRepository;
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
        if (quest.getQuestType() == QuestType.TEAM) {
            return verifyTeamQuest(user, quest);
        }
        return verifySoloQuest(user, quest);
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
        if (quest.getQuestType() == QuestType.TEAM) {
            return rewardTeamQuest(quest, targetUser, request);
        }
        return rewardSoloQuest(quest, targetUser, request);
    }

    private QuestResponseDTO.QuestVerifyResDTO verifySoloQuest(User user, Quest quest) {
        if (userQuestRepository.existsByQuestAndUserAndStatusIn(quest, user, BLOCKING_STATUSES)) {
            throw new QuestException(QuestErrorCode.SUBMISSION_ALREADY_EXISTS);
        }
        try {
            UserQuest saved = userQuestRepository.save(UserQuest.builder()
                    .quest(quest)
                    .user(user)
                    .status(UserQuestStatus.PENDING)
                    .build());
            return new QuestResponseDTO.QuestVerifyResDTO(
                    saved.getId(),
                    quest.getId(),
                    saved.getStatus(),
                    saved.getCreatedAt()
            );
        } catch (DataIntegrityViolationException ex) {
            throw new QuestException(QuestErrorCode.SUBMISSION_ALREADY_EXISTS);
        }
    }

    private QuestResponseDTO.QuestVerifyResDTO verifyTeamQuest(User user, Quest quest) {
        BuddyMatchRequest match = findAcceptedMatch(user);
        if (buddyMatchQuestRepository.existsByQuestAndBuddyMatchAndStatusIn(quest, match, BLOCKING_STATUSES)) {
            throw new QuestException(QuestErrorCode.SUBMISSION_ALREADY_EXISTS);
        }
        try {
            BuddyMatchQuest saved = buddyMatchQuestRepository.save(BuddyMatchQuest.builder()
                    .quest(quest)
                    .buddyMatch(match)
                    .status(UserQuestStatus.PENDING)
                    .build());
            return new QuestResponseDTO.QuestVerifyResDTO(
                    saved.getId(),
                    quest.getId(),
                    saved.getStatus(),
                    saved.getCreatedAt()
            );
        } catch (DataIntegrityViolationException ex) {
            throw new QuestException(QuestErrorCode.SUBMISSION_ALREADY_EXISTS);
        }
    }

    private QuestResponseDTO.QuestRewardResDTO rewardSoloQuest(
            Quest quest,
            User targetUser,
            QuestRequestDTO.QuestRewardReqDTO request
    ) {
        UserQuest userQuest = userQuestRepository
                .findByQuestAndUserAndStatus(quest, targetUser, UserQuestStatus.PENDING)
                .orElseThrow(() -> new QuestException(QuestErrorCode.SUBMISSION_NOT_FOUND));
        if (request.action() == QuestRewardAction.REJECT) {
            userQuest.updateStatus(UserQuestStatus.REJECTED);
            return new QuestResponseDTO.QuestRewardResDTO(
                    userQuest.getId(),
                    quest.getId(),
                    targetUser.getId(),
                    userQuest.getStatus(),
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
                USER_QUEST_REF_TYPE,
                userQuest.getId()
        )) {
            throw new QuestException(QuestErrorCode.REWARD_ALREADY_GRANTED);
        }
        userQuest.updateStatus(UserQuestStatus.APPROVED);
        return grantReward(targetUser, quest, userQuest.getId(), USER_QUEST_REF_TYPE, userQuest.getStatus());
    }

    private QuestResponseDTO.QuestRewardResDTO rewardTeamQuest(
            Quest quest,
            User targetUser,
            QuestRequestDTO.QuestRewardReqDTO request
    ) {
        BuddyMatchRequest match = findAcceptedMatch(targetUser);
        BuddyMatchQuest buddyQuest = buddyMatchQuestRepository
                .findByQuestAndBuddyMatchAndStatus(quest, match, UserQuestStatus.PENDING)
                .orElseThrow(() -> new QuestException(QuestErrorCode.SUBMISSION_NOT_FOUND));
        if (request.action() == QuestRewardAction.REJECT) {
            buddyQuest.updateStatus(UserQuestStatus.REJECTED);
            return new QuestResponseDTO.QuestRewardResDTO(
                    buddyQuest.getId(),
                    quest.getId(),
                    targetUser.getId(),
                    buddyQuest.getStatus(),
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
                BUDDY_MATCH_QUEST_REF_TYPE,
                buddyQuest.getId()
        )) {
            throw new QuestException(QuestErrorCode.REWARD_ALREADY_GRANTED);
        }
        buddyQuest.updateStatus(UserQuestStatus.APPROVED);
        return grantReward(targetUser, quest, buddyQuest.getId(), BUDDY_MATCH_QUEST_REF_TYPE, buddyQuest.getStatus());
    }

    private QuestResponseDTO.QuestRewardResDTO grantReward(
            User targetUser,
            Quest quest,
            Long refId,
            String refType,
            UserQuestStatus status
    ) {
        int rewardPoints = quest.getRewardPoints();
        targetUser.adjustPoint(rewardPoints);
        int balanceAfter = targetUser.getPointBalance();
        try {
            pointLedgerRepository.save(PointLedger.builder()
                    .user(targetUser)
                    .amount(rewardPoints)
                    .reason(PointReason.QUEST_REWARD)
                    .refType(refType)
                    .refId(refId)
                    .balanceAfter(balanceAfter)
                    .build());
        } catch (DataIntegrityViolationException ex) {
            throw new QuestException(QuestErrorCode.REWARD_ALREADY_GRANTED);
        }
        return new QuestResponseDTO.QuestRewardResDTO(
                refId,
                quest.getId(),
                targetUser.getId(),
                status,
                rewardPoints,
                balanceAfter
        );
    }

    private BuddyMatchRequest findAcceptedMatch(User user) {
        return buddyMatchRequestRepository.findByParticipantAndStatus(user, BuddyMatchStatus.ACCEPTED)
                .stream()
                .findFirst()
                .orElseThrow(() -> new QuestException(QuestErrorCode.MATCH_REQUIRED));
    }

    private QuestResponseDTO.DailyQuestItemDTO toDailyQuestItem(Quest quest, User user) {
        UserQuestStatus status = resolveStatus(quest, user);
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

    private UserQuestStatus resolveStatus(Quest quest, User user) {
        if (quest.getQuestType() == QuestType.TEAM) {
            return buddyMatchRequestRepository.findByParticipantAndStatus(user, BuddyMatchStatus.ACCEPTED)
                    .stream()
                    .findFirst()
                    .flatMap(match -> buddyMatchQuestRepository
                            .findFirstByQuestAndBuddyMatchOrderByCreatedAtDesc(quest, match)
                            .map(BuddyMatchQuest::getStatus))
                    .orElse(null);
        }
        return userQuestRepository.findFirstByQuestAndUserOrderByCreatedAtDesc(quest, user)
                .map(UserQuest::getStatus)
                .orElse(null);
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
