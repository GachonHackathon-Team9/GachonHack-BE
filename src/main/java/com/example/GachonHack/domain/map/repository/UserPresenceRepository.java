package com.example.GachonHack.domain.map.repository;

import com.example.GachonHack.domain.map.entity.UserPresence;
import com.example.GachonHack.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPresenceRepository extends JpaRepository<UserPresence, Long> {

    Optional<UserPresence> findByUser(User user);
}
