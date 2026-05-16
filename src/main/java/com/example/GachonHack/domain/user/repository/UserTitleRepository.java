package com.example.GachonHack.domain.user.repository;

import com.example.GachonHack.domain.shop.entity.TitleCatalog;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.domain.user.entity.UserTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTitleRepository extends JpaRepository<UserTitle, Long> {

    @Query("SELECT ut FROM UserTitle ut JOIN FETCH ut.title WHERE ut.user = :user")
    List<UserTitle> findByUserWithTitle(@Param("user") User user);

    @Query("SELECT ut FROM UserTitle ut JOIN FETCH ut.title WHERE ut.id = :id AND ut.user = :user")
    Optional<UserTitle> findByIdAndUser(@Param("id") Long id, @Param("user") User user);

    boolean existsByUserAndTitle(User user, TitleCatalog title);

    List<UserTitle> findByUser(User user);
}
