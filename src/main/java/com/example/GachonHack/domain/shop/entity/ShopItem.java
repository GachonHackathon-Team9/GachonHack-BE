package com.example.GachonHack.domain.shop.entity;

import com.example.GachonHack.domain.shop.enums.ItemType;
import com.example.GachonHack.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shop_items")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ShopItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 10)
    private ItemType itemType;

    // badge_catalog 또는 title_catalog의 PK (item_type으로 구분)
    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "price_points", nullable = false)
    private Integer pricePoints;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_on_sale", nullable = false)
    private boolean onSale;
}
