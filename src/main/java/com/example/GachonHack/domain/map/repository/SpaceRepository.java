package com.example.GachonHack.domain.map.repository;

import com.example.GachonHack.domain.map.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {

    Optional<Space> findByCode(String code);
}
