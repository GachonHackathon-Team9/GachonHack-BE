package com.example.GachonHack.domain.community.repository;

import com.example.GachonHack.domain.community.entity.BuddyMatchRequest;
import com.example.GachonHack.domain.community.entity.ChatRoom;
import com.example.GachonHack.domain.map.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
            SELECT c FROM ChatRoom c
            WHERE c.space = :space AND c.buddyMatch IS NULL AND c.active = true
            ORDER BY c.createdAt DESC
            """)
    Optional<ChatRoom> findFirstActivePublicSpaceRoom(@Param("space") Space space);

    @Query("""
            SELECT c FROM ChatRoom c
            LEFT JOIN FETCH c.buddyMatch bm
            LEFT JOIN FETCH bm.requester
            LEFT JOIN FETCH bm.target
            WHERE c.space = :space AND c.active = true
            ORDER BY c.createdAt DESC
            """)
    Optional<ChatRoom> findFirstActiveBySpaceWithParticipants(@Param("space") Space space);

    Optional<ChatRoom> findByBuddyMatchAndActiveTrue(BuddyMatchRequest buddyMatch);
}
