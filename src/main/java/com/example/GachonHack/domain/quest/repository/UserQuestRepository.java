package com.example.GachonHack.domain.quest.repository;

import com.example.GachonHack.domain.quest.entity.Quest;
import com.example.GachonHack.domain.quest.entity.UserQuest;
import com.example.GachonHack.domain.quest.enums.UserQuestStatus;
import com.example.GachonHack.domain.user.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface UserQuestRepository extends JpaRepository<UserQuest, Long> {

    Optional<UserQuest> findFirstByQuestAndUserOrderByCreatedAtDesc(Quest quest, User user);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UserQuest> findByQuestAndUserAndStatus(Quest quest, User user, UserQuestStatus status);

    boolean existsByQuestAndUserAndStatusIn(Quest quest, User user, Collection<UserQuestStatus> statuses);
}
