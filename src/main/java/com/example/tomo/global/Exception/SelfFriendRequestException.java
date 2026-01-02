package com.example.tomo.global.Exception;

import org.springframework.http.HttpStatus;

public class SelfFriendRequestException extends BusinessException {

    public SelfFriendRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
