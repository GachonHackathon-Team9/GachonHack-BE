package com.example.GachonHack.domain.quest.repository;

import com.example.GachonHack.domain.community.entity.BuddyMatchRequest;
import com.example.GachonHack.domain.quest.entity.BuddyMatchQuest;
import com.example.GachonHack.domain.quest.entity.Quest;
import com.example.GachonHack.domain.quest.enums.UserQuestStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface BuddyMatchQuestRepository extends JpaRepository<BuddyMatchQuest, Long> {

    Optional<BuddyMatchQuest> findFirstByQuestAndBuddyMatchOrderByCreatedAtDesc(Quest quest, BuddyMatchRequest buddyMatch);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BuddyMatchQuest> findByQuestAndBuddyMatchAndStatus(
            Quest quest,
            BuddyMatchRequest buddyMatch,
            UserQuestStatus status
    );

    boolean existsByQuestAndBuddyMatchAndStatusIn(
            Quest quest,
            BuddyMatchRequest buddyMatch,
            Collection<UserQuestStatus> statuses
    );
}
