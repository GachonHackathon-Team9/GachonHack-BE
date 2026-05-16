package com.example.GachonHack.domain.quest.entity;

import com.example.GachonHack.domain.community.entity.BuddyMatchRequest;
import com.example.GachonHack.domain.quest.enums.UserQuestStatus;
import com.example.GachonHack.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "buddy_match_quests",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_buddy_match_quest_match_quest_status",
                columnNames = {"buddy_match_id", "quest_id", "status"}
        )
)
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BuddyMatchQuest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buddy_match_id", nullable = false)
    private BuddyMatchRequest buddyMatch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_id", nullable = false)
    private Quest quest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserQuestStatus status;

    public void updateStatus(UserQuestStatus status) {
        this.status = status;
    }
}
