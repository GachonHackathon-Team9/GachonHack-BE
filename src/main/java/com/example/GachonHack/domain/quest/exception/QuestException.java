package com.example.GachonHack.domain.quest.exception;

import com.example.GachonHack.global.apiPayload.code.BaseCode;
import com.example.GachonHack.global.apiPayload.exception.GeneralException;

public class QuestException extends GeneralException {
    public QuestException(BaseCode code) {
        super(code);
    }
}
