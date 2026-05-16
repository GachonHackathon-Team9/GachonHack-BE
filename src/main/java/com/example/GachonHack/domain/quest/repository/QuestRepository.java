package com.example.GachonHack.domain.quest.repository;

import com.example.GachonHack.domain.quest.entity.Quest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestRepository extends JpaRepository<Quest, Long> {

    List<Quest> findByDailyTrueAndActiveTrueOrderByIdAsc();

    List<Quest> findByDailyTrueOrderByIdAsc();
}
