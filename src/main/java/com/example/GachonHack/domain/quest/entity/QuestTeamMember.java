package com.example.GachonHack.domain.quest.entity;

import com.example.GachonHack.domain.quest.enums.TeamMemberRole;
import com.example.GachonHack.domain.quest.enums.TeamMemberStatus;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quest_team_members")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestTeamMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private QuestTeam team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TeamMemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_status", nullable = false, length = 20)
    private TeamMemberStatus memberStatus;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    public void leave() {
        this.memberStatus = TeamMemberStatus.LEFT;
        this.leftAt = LocalDateTime.now();
    }
}
