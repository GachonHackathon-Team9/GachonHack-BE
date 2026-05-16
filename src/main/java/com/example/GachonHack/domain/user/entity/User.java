package com.example.GachonHack.domain.user.entity;

import com.example.GachonHack.domain.user.enums.CatType;
import com.example.GachonHack.domain.user.enums.Role;
import com.example.GachonHack.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kakao_id", length = 64, unique = true)
    private String kakaoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "cat_type", length = 20)
    private CatType catType;

    @Column(name = "real_name", length = 50)
    private String realName;

    @Column(name = "student_id", length = 20)
    private String studentId;

    @Column(name = "nickname", length = 6, unique = true)
    private String nickname;

    @Column(name = "grade")
    private Short grade;

    @Column(name = "point_balance", nullable = false)
    @Builder.Default
    private Integer pointBalance = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "cat_type", length = 20)
    private CatType catType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;

    public void completeProfile(String realName, String studentId, Short grade, String nickname, CatType catType) {
        this.realName = realName;
        this.studentId = studentId;
        this.grade = grade;
        this.nickname = nickname;
        this.catType = catType;
    }

    public void adjustPoint(int delta) {
        this.pointBalance += delta;
    }
}
