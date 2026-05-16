package com.example.GachonHack.domain.map.repository;

import com.example.GachonHack.domain.map.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {

    Optional<Space> findByCode(String code);

    List<Space> findByTypeOrderBySortOrderAsc(String type);

    List<Space> findByParentAndTypeOrderBySortOrderAsc(Space parent, String type);

    List<Space> findByParentAndTypeInOrderBySortOrderAsc(Space parent, Collection<String> types);
}
