package com.example.GachonHack.domain.shop.dto.res;

import java.util.List;

public class ShopResponseDTO {

    public record ShopItemDTO(
            Long shopItemId,
            Long titleId,
            String displayText,
            Integer pricePoints,
            Integer sortOrder
    ) {}

    public record ShopItemListResDTO(
            List<ShopItemDTO> items
    ) {}

    public record PurchaseResDTO(
            Long orderId,
            Long userTitleId,
            Integer balanceAfter
    ) {}
}
