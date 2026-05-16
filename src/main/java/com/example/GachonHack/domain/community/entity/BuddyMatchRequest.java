package com.example.GachonHack.domain.community.entity;

import com.example.GachonHack.domain.community.enums.BuddyMatchStatus;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "buddy_match_requests")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BuddyMatchRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private User target;

    @Column(name = "mission_id")
    private Long missionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BuddyMatchStatus status;

    @Column(name = "requester_grade")
    private Short requesterGrade;

    @Column(name = "target_grade")
    private Short targetGrade;

    public void respond(BuddyMatchStatus status) {
        this.status = status;
    }
}
