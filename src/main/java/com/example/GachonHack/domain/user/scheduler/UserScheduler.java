package com.example.GachonHack.domain.user.scheduler;

import com.example.GachonHack.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserScheduler {

    private final UserRepository userRepository;

    // 매년 1월 1일 00:00:00 — 4학년 미만 사용자 학년 일괄 상향
    @Scheduled(cron = "0 0 0 1 1 *", zone = "Asia/Seoul")
    @Transactional
    public void incrementGrade() {
        int updated = userRepository.incrementGradeUnderFour();
        log.info("[학년 갱신] {}명 완료", updated);
    }
}
