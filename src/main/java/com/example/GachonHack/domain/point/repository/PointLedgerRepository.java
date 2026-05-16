package com.example.GachonHack.domain.point.repository;

import com.example.GachonHack.domain.point.entity.PointLedger;
import com.example.GachonHack.domain.point.enums.PointReason;
import com.example.GachonHack.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointLedgerRepository extends JpaRepository<PointLedger, Long> {

    boolean existsByUserAndReasonAndRefTypeAndRefId(
            User user,
            PointReason reason,
            String refType,
            Long refId
    );
}
