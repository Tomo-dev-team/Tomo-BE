package com.example.tomo.global.S3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum S3ErrorCode {

    S3_UPLOAD_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "이미지 업로드에 실패했습니다."
    ),

    INVALID_FILE(
            HttpStatus.BAD_REQUEST,
            "유효하지 않은 파일입니다."
    );

    private final HttpStatus status;
    private final String message;
}

