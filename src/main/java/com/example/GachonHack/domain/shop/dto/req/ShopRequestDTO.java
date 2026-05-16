package com.example.GachonHack.domain.shop.dto.req;

import jakarta.validation.constraints.NotNull;

public class ShopRequestDTO {

    public record PurchaseReqDTO(
            @NotNull Long titleId
    ) {}
}
