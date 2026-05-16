package com.example.GachonHack.global.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    private Long userId;

    // 토큰 원문이 아닌 SHA-256 해시값만 저장
    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    private RefreshToken(Long userId, String tokenHash) {
        this.userId = userId;
        this.tokenHash = tokenHash;
    }

    public static RefreshToken of(Long userId, String tokenHash) {
        return new RefreshToken(userId, tokenHash);
    }

    public void updateTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }
}
