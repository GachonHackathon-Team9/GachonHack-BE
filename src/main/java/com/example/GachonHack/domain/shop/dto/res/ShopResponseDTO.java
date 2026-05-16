package com.example.GachonHack.domain.shop.dto.res;

import java.util.List;

public class ShopResponseDTO {

    public record TitleItemDTO(
            Long titleId,
            String displayText,
            Integer pricePoints,
            Integer sortOrder
    ) {}

    public record TitleItemListResDTO(
            List<TitleItemDTO> items
    ) {}

    public record PurchaseResDTO(
            Long orderId,
            Long userTitleId,
            Integer balanceAfter
    ) {}
}
