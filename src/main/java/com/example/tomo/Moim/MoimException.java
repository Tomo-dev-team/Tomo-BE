package com.example.tomo.Moim;

import com.example.tomo.global.Exception.BusinessException;
import lombok.Getter;

@Getter
public class MoimException extends BusinessException {

    private final MoimErrorCode errorCode;

    public MoimException(MoimErrorCode errorCode) {
        super(
                errorCode.getMessage(),
                errorCode.getStatus()
        );
        this.errorCode = errorCode;
    }
}


