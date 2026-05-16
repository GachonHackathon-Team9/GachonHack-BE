package com.example.GachonHack.domain.user.repository;

import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.domain.user.entity.UserTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTitleRepository extends JpaRepository<UserTitle, Long> {

    @Query("SELECT ut FROM UserTitle ut JOIN FETCH ut.title WHERE ut.user = :user")
    List<UserTitle> findByUserWithTitle(@Param("user") User user);
}
