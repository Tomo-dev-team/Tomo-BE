package com.example.tomo.Users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode {

    // 인증 / 권한
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "로그인된 사용자가 아닙니다"),

    // 회원
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다"),
    DUPLICATE_EMAIL(HttpStatus.UNPROCESSABLE_ENTITY, "같은 이메일로 가입 내역이 존재합니다"),

    // 인증 토큰
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "리프레쉬 토큰이 올바르지 않습니다"),

    INVALID_QUERY(HttpStatus.BAD_REQUEST, "email 또는 inviteCode 중 하나만 전달해야 합니다");

    private final HttpStatus status;
    private final String message;
}

