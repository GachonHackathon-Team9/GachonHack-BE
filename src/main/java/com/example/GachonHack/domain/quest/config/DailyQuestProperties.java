package com.example.GachonHack.domain.quest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.ZoneId;

@ConfigurationProperties(prefix = "quest.daily")
public record DailyQuestProperties(
        String zone,
        int activeCount,
        String refreshCron
) {
    public ZoneId zoneId() {
        return ZoneId.of(zone);
    }
}
