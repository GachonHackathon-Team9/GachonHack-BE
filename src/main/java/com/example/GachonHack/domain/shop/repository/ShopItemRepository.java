package com.example.GachonHack.domain.shop.repository;

import com.example.GachonHack.domain.shop.entity.ShopItem;
import com.example.GachonHack.domain.shop.enums.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopItemRepository extends JpaRepository<ShopItem, Long> {

    List<ShopItem> findByOnSaleTrueAndItemTypeOrderBySortOrderAsc(ItemType itemType);
}
