package com.example.GachonHack.domain.quest.entity;

import com.example.GachonHack.domain.quest.enums.UserQuestStatus;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_quests",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_quest_quest_user_status",
                columnNames = {"quest_id", "user_id", "status"}
        )
)
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserQuest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_id", nullable = false)
    private Quest quest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserQuestStatus status;

    public void updateStatus(UserQuestStatus status) {
        this.status = status;
    }
}
