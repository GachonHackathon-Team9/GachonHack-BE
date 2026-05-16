package com.example.GachonHack.domain.shop.service;

import com.example.GachonHack.domain.point.entity.PointLedger;
import com.example.GachonHack.domain.point.enums.PointReason;
import com.example.GachonHack.domain.point.repository.PointLedgerRepository;
import com.example.GachonHack.domain.shop.dto.req.ShopRequestDTO;
import com.example.GachonHack.domain.shop.dto.res.ShopResponseDTO;
import com.example.GachonHack.domain.shop.entity.ShopItem;
import com.example.GachonHack.domain.shop.entity.ShopOrder;
import com.example.GachonHack.domain.shop.entity.TitleCatalog;
import com.example.GachonHack.domain.shop.enums.ItemType;
import com.example.GachonHack.domain.shop.enums.OrderStatus;
import com.example.GachonHack.domain.shop.exception.ShopException;
import com.example.GachonHack.domain.shop.exception.code.ShopErrorCode;
import com.example.GachonHack.domain.shop.repository.ShopItemRepository;
import com.example.GachonHack.domain.shop.repository.ShopOrderRepository;
import com.example.GachonHack.domain.shop.repository.TitleCatalogRepository;
import com.example.GachonHack.domain.user.entity.User;
import com.example.GachonHack.domain.user.entity.UserTitle;
import com.example.GachonHack.domain.user.repository.UserRepository;
import com.example.GachonHack.domain.user.repository.UserTitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {

    private static final String SHOP_ORDER_REF_TYPE = "SHOP_ORDER";

    private final ShopItemRepository shopItemRepository;
    private final TitleCatalogRepository titleCatalogRepository;
    private final ShopOrderRepository shopOrderRepository;
    private final UserRepository userRepository;
    private final UserTitleRepository userTitleRepository;
    private final PointLedgerRepository pointLedgerRepository;

    @Transactional(readOnly = true)
    public ShopResponseDTO.ShopItemListResDTO getItems() {
        List<ShopResponseDTO.ShopItemDTO> items = shopItemRepository
                .findByOnSaleTrueAndItemTypeOrderBySortOrderAsc(ItemType.TITLE)
                .stream()
                .map(this::toItemDto)
                .toList();
        return new ShopResponseDTO.ShopItemListResDTO(items);
    }

    @Transactional
    public ShopResponseDTO.PurchaseResDTO purchase(Long userId, ShopRequestDTO.PurchaseReqDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ShopException(ShopErrorCode.USER_NOT_FOUND));
        ShopItem shopItem = shopItemRepository.findById(request.shopItemId())
                .orElseThrow(() -> new ShopException(ShopErrorCode.ITEM_NOT_FOUND));
        if (!shopItem.isOnSale() || shopItem.getItemType() != ItemType.TITLE) {
            throw new ShopException(ShopErrorCode.NOT_ON_SALE);
        }
        TitleCatalog title = titleCatalogRepository.findById(shopItem.getItemId())
                .orElseThrow(() -> new ShopException(ShopErrorCode.TITLE_NOT_FOUND));
        if (userTitleRepository.existsByUserAndTitle(user, title)) {
            throw new ShopException(ShopErrorCode.ALREADY_OWNED);
        }
        int price = shopItem.getPricePoints();
        if (user.getPointBalance() < price) {
            throw new ShopException(ShopErrorCode.INSUFFICIENT_POINTS);
        }
        user.adjustPoint(-price);
        int balanceAfter = user.getPointBalance();
        ShopOrder order = shopOrderRepository.save(ShopOrder.builder()
                .user(user)
                .shop(shopItem.getShop())
                .shopItem(shopItem)
                .itemType(ItemType.TITLE)
                .itemId(title.getId())
                .pricePoints(price)
                .status(OrderStatus.COMPLETED)
                .build());
        pointLedgerRepository.save(PointLedger.builder()
                .user(user)
                .amount(-price)
                .reason(PointReason.SHOP_PURCHASE)
                .refType(SHOP_ORDER_REF_TYPE)
                .refId(order.getId())
                .balanceAfter(balanceAfter)
                .build());
        UserTitle userTitle = userTitleRepository.save(UserTitle.builder()
                .user(user)
                .title(title)
                .source("SHOP")
                .equipped(false)
                .acquiredAt(LocalDateTime.now())
                .build());
        return new ShopResponseDTO.PurchaseResDTO(order.getId(), userTitle.getId(), balanceAfter);
    }

    private ShopResponseDTO.ShopItemDTO toItemDto(ShopItem item) {
        TitleCatalog title = titleCatalogRepository.findById(item.getItemId())
                .orElseThrow(() -> new ShopException(ShopErrorCode.TITLE_NOT_FOUND));
        return new ShopResponseDTO.ShopItemDTO(
                item.getId(),
                title.getId(),
                title.getDisplayText(),
                item.getPricePoints(),
                item.getSortOrder()
        );
    }
}
