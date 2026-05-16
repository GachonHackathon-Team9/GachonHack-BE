package com.example.GachonHack.global.apiPayload.exception;

import com.example.GachonHack.global.apiPayload.code.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException{
    private final BaseCode code;
}
