package com.example.GachonHack.domain.shop.exception;

import com.example.GachonHack.global.apiPayload.code.BaseCode;
import com.example.GachonHack.global.apiPayload.exception.GeneralException;

public class ShopException extends GeneralException {
    public ShopException(BaseCode code) {
        super(code);
    }
}
