package com.example.tomo.Friends;

import com.example.tomo.global.Exception.BusinessException;
import lombok.Getter;

@Getter
public class FriendException extends BusinessException {

    private final FriendErrorCode errorCode;

    public FriendException(FriendErrorCode errorCode) {
        super(
                errorCode.getMessage(),
                errorCode.getStatus()
        );
        this.errorCode = errorCode;
    }
}

