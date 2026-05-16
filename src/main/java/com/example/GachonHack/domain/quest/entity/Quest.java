package com.example.GachonHack.domain.quest.entity;

import com.example.GachonHack.domain.map.entity.Space;
import com.example.GachonHack.domain.quest.enums.QuestType;
import com.example.GachonHack.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quests")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Quest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "quest_type", nullable = false, length = 10)
    private QuestType questType;

    @Column(name = "reward_points", nullable = false)
    private Integer rewardPoints;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    private Space space;

    @Column(name = "required_minutes")
    private Integer requiredMinutes;

    @Column(name = "is_daily", nullable = false)
    private boolean daily;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    public void updateActive(boolean active) {
        this.active = active;
    }
}
