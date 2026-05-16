package com.example.GachonHack.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_auth",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_auth_provider_uid",
                columnNames = {"provider", "provider_uid"}
        )
)
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserAuth {

    public static final String PROVIDER_KAKAO = "KAKAO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String provider;

    @Column(name = "provider_uid", nullable = false, length = 128)
    private String providerUid;
}
