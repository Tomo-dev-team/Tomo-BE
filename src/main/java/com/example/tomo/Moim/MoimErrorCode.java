package com.example.tomo.Moim;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MoimErrorCode {

    MOIM_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 모임입니다"
    ),

    USER_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 사용자입니다"
    ),

    NOT_MOIM_LEADER(
            HttpStatus.FORBIDDEN,
            "모임 삭제는 리더만 가능합니다"
    );

    private final HttpStatus status;
    private final String message;
}
