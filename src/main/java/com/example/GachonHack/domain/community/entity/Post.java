package com.example.GachonHack.domain.community.entity;

import com.example.GachonHack.domain.community.enums.PostAuthority;
import com.example.GachonHack.domain.community.enums.PostType;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "posts")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private Integer likeCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PostType type;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PostAuthority authority;

    public void increaseViewCount() {
        this.viewCount++;
    }
}
