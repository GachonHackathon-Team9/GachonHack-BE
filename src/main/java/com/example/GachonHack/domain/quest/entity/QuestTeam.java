package com.example.GachonHack.domain.quest.entity;

import com.example.GachonHack.domain.quest.enums.TeamStatus;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quest_teams")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestTeam extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_id", nullable = false)
    private Quest quest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_user_id", nullable = false)
    private User leader;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TeamStatus status;

    @Column(name = "recruit_deadline")
    private LocalDateTime recruitDeadline;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "min_team_size")
    private Integer minTeamSize;

    @Column(name = "max_team_size")
    private Integer maxTeamSize;

    public void complete() {
        this.status = TeamStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
}
