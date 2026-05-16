package com.example.GachonHack.domain.community.repository;

import com.example.GachonHack.domain.community.entity.ChatRoom;
import com.example.GachonHack.domain.map.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findFirstBySpaceAndActiveTrueOrderByCreatedAtDesc(Space space);
}
