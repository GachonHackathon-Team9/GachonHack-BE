package com.example.GachonHack.domain.map.exception;

import com.example.GachonHack.global.apiPayload.code.BaseCode;
import com.example.GachonHack.global.apiPayload.exception.GeneralException;

public class MapException extends GeneralException {
    public MapException(BaseCode code) {
        super(code);
    }
}
