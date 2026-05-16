package com.example.GachonHack.domain.quest.service;

import com.example.GachonHack.domain.quest.config.DailyQuestProperties;
import com.example.GachonHack.domain.quest.entity.Quest;
import com.example.GachonHack.domain.quest.repository.QuestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyQuestRefreshService {

    private final QuestRepository questRepository;
    private final DailyQuestProperties dailyQuestProperties;

    @Transactional
    public void refreshDailyQuests() {
        List<Quest> pool = questRepository.findByDailyTrueOrderByIdAsc();
        if (pool.isEmpty()) {
            log.warn("일일 퀘스트 갱신 스킵: is_daily=true 인 퀘스트가 없습니다.");
            return;
        }

        pool.forEach(quest -> quest.updateActive(false));

        List<Quest> candidates = new ArrayList<>(pool);
        Collections.shuffle(candidates);
        int pickCount = Math.min(dailyQuestProperties.activeCount(), candidates.size());
        for (int i = 0; i < pickCount; i++) {
            candidates.get(i).updateActive(true);
        }

        log.info("일일 퀘스트 갱신 완료: pool={}, activated={}", pool.size(), pickCount);
    }
}
