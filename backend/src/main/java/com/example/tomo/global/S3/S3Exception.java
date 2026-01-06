package com.example.tomo.global.S3;

import com.example.tomo.global.Exception.BusinessException;
import lombok.Getter;

@Getter
public class S3Exception extends BusinessException {

    private final S3ErrorCode errorCode;

    public S3Exception(S3ErrorCode errorCode) {
        super(
                errorCode.getMessage(),
                errorCode.getStatus()
        );
        this.errorCode = errorCode;
    }

}
