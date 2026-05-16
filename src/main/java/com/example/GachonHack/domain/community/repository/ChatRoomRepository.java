package com.example.GachonHack.domain.community.repository;

import com.example.GachonHack.domain.community.entity.BuddyMatchRequest;
import com.example.GachonHack.domain.community.entity.ChatRoom;
import com.example.GachonHack.domain.map.entity.Space;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
            SELECT c FROM ChatRoom c
            WHERE c.space = :space AND c.buddyMatch IS NULL AND c.active = true
            ORDER BY c.createdAt DESC
            """)
    List<ChatRoom> findActivePublicSpaceRooms(@Param("space") Space space, Pageable pageable);

    @Query("""
            SELECT c FROM ChatRoom c
            LEFT JOIN FETCH c.buddyMatch bm
            LEFT JOIN FETCH bm.requester
            LEFT JOIN FETCH bm.target
            WHERE c.space = :space AND c.active = true
            ORDER BY c.createdAt DESC
            """)
    List<ChatRoom> findActiveBySpaceWithParticipants(@Param("space") Space space, Pageable pageable);

    Optional<ChatRoom> findByBuddyMatchAndActiveTrue(BuddyMatchRequest buddyMatch);

    @Query("""
            SELECT c FROM ChatRoom c
            JOIN FETCH c.space s
            WHERE c.active = true
              AND c.buddyMatch IS NULL
              AND s.id = :spaceId
            ORDER BY c.id ASC
            """)
    List<ChatRoom> findActiveSpaceChatRoomsBySpaceId(@Param("spaceId") Long spaceId);
}
