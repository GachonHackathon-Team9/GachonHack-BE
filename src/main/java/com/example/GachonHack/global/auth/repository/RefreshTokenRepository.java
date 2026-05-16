package com.example.GachonHack.global.auth.repository;

import com.example.GachonHack.global.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
