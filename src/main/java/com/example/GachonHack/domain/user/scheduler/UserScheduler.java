package com.example.GachonHack.domain.user.scheduler;

import com.example.GachonHack.domain.quest.config.DailyQuestProperties;
import com.example.GachonHack.domain.quest.repository.QuestRepository;
import com.example.GachonHack.domain.quest.service.DailyQuestRefreshService;
import com.example.GachonHack.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserScheduler {

    private final UserRepository userRepository;
    private final DailyQuestRefreshService dailyQuestRefreshService;
    private final DailyQuestProperties dailyQuestProperties;
    private final QuestRepository questRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void refreshDailyQuestsOnStartupIfEmpty() {
        if (questRepository.findByDailyTrueAndActiveTrueOrderByIdAsc().isEmpty()) {
            log.info("활성 일일 퀘스트가 없어 기동 시 1회 갱신합니다.");
            dailyQuestRefreshService.refreshDailyQuests();
        }
    }

    // 매일 00:00:00 — 일일 퀘스트 노출 갱신
    @Scheduled(cron = "${quest.daily.refresh-cron}", zone = "${quest.daily.zone}")
    public void refreshDailyQuests() {
        log.info("[일일 퀘스트 갱신] 시작 (zone={})", dailyQuestProperties.zone());
        dailyQuestRefreshService.refreshDailyQuests();
    }

    // 매년 1월 1일 00:00:00 — 4학년 미만 사용자 학년 일괄 상향
    @Scheduled(cron = "0 0 0 1 1 *", zone = "Asia/Seoul")
    @Transactional
    public void incrementGrade() {
        int updated = userRepository.incrementGradeUnderFour();
        log.info("[학년 갱신] {}명 완료", updated);
    }
}
