package com.example.GachonHack.domain.user.repository;

import com.example.GachonHack.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKakaoId(String kakaoId);

    boolean existsByNickname(String nickname);

    @Modifying
    @Query("UPDATE User u SET u.grade = u.grade + 1 WHERE u.grade IS NOT NULL AND u.grade < 4")
    int incrementGradeUnderFour();
}
