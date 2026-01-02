package com.example.tomo.global.AOP;

import com.example.tomo.Friends.FriendErrorCode;
import com.example.tomo.Friends.FriendException;
import com.example.tomo.Moim.MoimErrorCode;
import com.example.tomo.Moim.MoimException;
import com.example.tomo.Promise.PromiseErrorCode;
import com.example.tomo.Promise.PromiseException;
import com.example.tomo.Users.UserErrorCode;
import com.example.tomo.Users.UserException;
import com.example.tomo.global.Exception.BusinessException;
import com.example.tomo.global.Exception.SelfFriendRequestException;
import com.example.tomo.global.ReponseType.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FriendException.class)
    public ResponseEntity<ApiResponse<?>> handleFriendException(FriendException e) {
        FriendErrorCode code = e.getErrorCode();

        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.failure(
                        code.getMessage()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure( e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleEntityNotFound(EntityNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("서버 오류가 발생했습니다"));
    }

    @ExceptionHandler(MoimException.class)
    public ResponseEntity<ApiResponse<?>> handleMoimException(MoimException e) {
        MoimErrorCode code = e.getErrorCode();

        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.failure(
                        code.getMessage()
                ));
    }
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserException(UserException e) {

        UserErrorCode code = e.getErrorCode();

        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.failure(code.getMessage()));
    }
    @ExceptionHandler(SelfFriendRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleSelfFriend(SelfFriendRequestException e) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure( e.getMessage()));
    }

    @ExceptionHandler(PromiseException.class)
    public ResponseEntity<ApiResponse<Void>> handlePromiseException(PromiseException e) {

        PromiseErrorCode code = e.getErrorCode();

        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.failure(code.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        // 1. DTO에 정의된 validation message 추출
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("잘못된 요청입니다.");

        // 2. ApiResponse 실패 응답 생성
        ApiResponse<Void> response = ApiResponse.failure(
                errorMessage
        );

        // 3. 400 Bad Request 반환
        return ResponseEntity
                .badRequest()
                .body(response);
    }
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException e
    ) {
        return ResponseEntity
                .status(e.getStatus())
                .body(ApiResponse.failure(e.getMessage()));
    }


}

