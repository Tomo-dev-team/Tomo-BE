package com.example.tomo.Promise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PromiseErrorCode {

    MOIM_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "모임 생성 후 약속을 만들어 주세요"
    ),

    PROMISE_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "이미 존재하는 약속입니다"
    ),

    PROMISE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 약속입니다"
    );

    private final HttpStatus status;
    private final String message;
}

