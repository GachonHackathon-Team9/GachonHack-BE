package com.example.GachonHack.domain.quest.repository;

import com.example.GachonHack.domain.quest.entity.Quest;
import com.example.GachonHack.domain.quest.entity.QuestSubmission;
import com.example.GachonHack.domain.quest.enums.SubmissionStatus;
import com.example.GachonHack.domain.user.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface QuestSubmissionRepository extends JpaRepository<QuestSubmission, Long> {

    Optional<QuestSubmission> findFirstByQuestAndUserOrderBySubmittedAtDesc(Quest quest, User user);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<QuestSubmission> findByQuestAndUserAndStatus(Quest quest, User user, SubmissionStatus status);

    boolean existsByQuestAndUserAndStatusIn(Quest quest, User user, Collection<SubmissionStatus> statuses);
}
