package com.example.GachonHack.domain.quest.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DailyQuestProperties.class)
public class QuestConfig {
}
