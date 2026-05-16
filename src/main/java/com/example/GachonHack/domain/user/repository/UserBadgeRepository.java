package com.example.GachonHack.domain.user.repository;

import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.domain.user.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    @Query("SELECT ub FROM UserBadge ub JOIN FETCH ub.badge WHERE ub.user = :user")
    List<UserBadge> findByUserWithBadge(@Param("user") User user);
}
