package com.example.GachonHack.domain.community.exception;

import com.example.GachonHack.global.apiPayload.code.BaseCode;
import com.example.GachonHack.global.apiPayload.exception.GeneralException;

public class CommunityException extends GeneralException {
    public CommunityException(BaseCode code) {
        super(code);
    }
}
