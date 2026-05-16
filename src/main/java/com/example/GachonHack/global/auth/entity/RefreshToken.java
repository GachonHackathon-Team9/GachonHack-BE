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

    @Column(nullable = false, length = 1000)
    private String token;

    private RefreshToken(Long userId, String token){
        this.userId = userId;
        this.token = token;
    }

    public static RefreshToken of(Long userId, String token) {
        return new RefreshToken(userId, token);
    }

    public void updateToken(String token){
        this.token = token;
    }
}
