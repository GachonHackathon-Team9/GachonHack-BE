package com.example.GachonHack.domain.map.repository;

import com.example.GachonHack.domain.map.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {
}
