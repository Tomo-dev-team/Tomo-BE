package com.example.tomo.Users;

import com.example.tomo.global.Exception.BusinessException;
import lombok.Getter;

@Getter
public class UserException extends BusinessException {

    private final UserErrorCode errorCode;

    public UserException(UserErrorCode errorCode) {
        super(
                errorCode.getMessage(),
                errorCode.getStatus()
        );
        this.errorCode = errorCode;
    }
}

