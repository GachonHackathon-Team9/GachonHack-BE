package com.example.GachonHack.domain.community.repository;

import com.example.GachonHack.domain.community.entity.BuddyMatchRequest;
import com.example.GachonHack.domain.community.enums.BuddyMatchStatus;
import com.example.GachonHack.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuddyMatchRequestRepository extends JpaRepository<BuddyMatchRequest, Long> {

    @Query("""
            SELECT r FROM BuddyMatchRequest r
            JOIN FETCH r.requester
            JOIN FETCH r.target
            WHERE r.target = :user AND r.status = :status
            ORDER BY r.createdAt DESC
            """)
    List<BuddyMatchRequest> findByTargetAndStatus(
            @Param("user") User user,
            @Param("status") BuddyMatchStatus status
    );

    @Query("""
            SELECT r FROM BuddyMatchRequest r
            JOIN FETCH r.requester
            JOIN FETCH r.target
            WHERE (r.requester = :user OR r.target = :user) AND r.status = :status
            ORDER BY r.createdAt DESC
            """)
    List<BuddyMatchRequest> findByParticipantAndStatus(
            @Param("user") User user,
            @Param("status") BuddyMatchStatus status
    );

    @Query("""
            SELECT r FROM BuddyMatchRequest r
            JOIN FETCH r.requester
            JOIN FETCH r.target
            WHERE r.id = :id
            """)
    Optional<BuddyMatchRequest> findByIdWithUsers(@Param("id") Long id);

    boolean existsByRequesterAndTargetAndStatusIn(
            User requester,
            User target,
            List<BuddyMatchStatus> statuses
    );
}
