package com.example.GachonHack.domain.shop.repository;

import com.example.GachonHack.domain.shop.entity.TitleCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TitleCatalogRepository extends JpaRepository<TitleCatalog, Long> {
}
