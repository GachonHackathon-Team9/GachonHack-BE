package com.example.GachonHack.domain.user.exception;


import com.example.GachonHack.global.apiPayload.code.BaseCode;
import com.example.GachonHack.global.apiPayload.exception.GeneralException;

public class UserException extends GeneralException {
    public UserException(BaseCode code){
        super(code);
    }
}
