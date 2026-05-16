package com.example.GachonHack.global.auth.exception;

import com.example.GachonHack.global.apiPayload.code.BaseCode;
import com.example.GachonHack.global.apiPayload.exception.GeneralException;

public class AuthException extends GeneralException {
    public AuthException(BaseCode code) {
        super(code);
    }
}
