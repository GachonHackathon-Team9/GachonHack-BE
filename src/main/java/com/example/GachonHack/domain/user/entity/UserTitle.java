package com.example.GachonHack.domain.user.entity;

import com.example.GachonHack.domain.shop.entity.TitleCatalog;
import com.example.GachonHack.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_titles")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserTitle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "title_id", nullable = false)
    private TitleCatalog title;

    @Column(nullable = false, length = 20)
    private String source;

    @Column(name = "is_equipped", nullable = false)
    private boolean equipped;

    public void toggleEquipped() {
        this.equipped = !this.equipped;
    }
}
