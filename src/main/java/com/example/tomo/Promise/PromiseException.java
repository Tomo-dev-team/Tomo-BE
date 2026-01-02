package com.example.tomo.Promise;

import com.example.tomo.global.Exception.BusinessException;
import lombok.Getter;

@Getter
public class PromiseException extends BusinessException {

    private final PromiseErrorCode errorCode;

    public PromiseException(PromiseErrorCode errorCode) {
        super(
                errorCode.getMessage(),
                errorCode.getStatus()
        );
        this.errorCode = errorCode;
    }
}

