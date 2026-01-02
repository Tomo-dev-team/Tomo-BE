package com.example.tomo.Users;

import com.example.tomo.Users.dtos.RequestUserSignDto;
import com.example.tomo.Users.dtos.ResponsePostUniformDto;
import com.example.tomo.firebase.ResponseFirebaseLoginDto;
import com.example.tomo.global.AuthService;
import com.example.tomo.global.ReponseType.ApiResponse;
import com.example.tomo.global.ReponseType.NoDataApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@Tag(name = "User API", description = "사용자 회원가입, 친구 관리, 인증 API")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/public/signup")
    public ResponseEntity<ResponsePostUniformDto> signUser(
            @Valid @RequestBody RequestUserSignDto dto) {
        return ResponseEntity.ok(userService.signUser(dto));
    }

    @DeleteMapping("/public/users")
    public ResponseEntity<NoDataApiResponse> deleteMyAccount(
            @AuthenticationPrincipal String uid) {
        userService.deleteUser(uid);
        return ResponseEntity.ok(NoDataApiResponse.success("계정이 삭제되었습니다."));
    }

    @PostMapping("/api/auth/firebase-login")
    public ResponseEntity<ApiResponse<ResponseFirebaseLoginDto>> login(
            @AuthenticationPrincipal String uid) {

        ResponseFirebaseLoginDto tokens = authService.loginWithFirebase(uid);
        return ResponseEntity.ok(ApiResponse.success(tokens, "로그인 성공"));
    }

    @PostMapping("/api/auth/refresh")
    public ResponseEntity<ApiResponse<ResponseFirebaseLoginDto>> refreshToken(
            @RequestHeader("Refresh-Token") String refreshTokenHeader) {

        ResponseFirebaseLoginDto tokens = authService.reissueAccessToken(refreshTokenHeader);
        return ResponseEntity.ok(ApiResponse.success(tokens, "Access token 재발급 성공"));
    }

    @DeleteMapping("/public/logout")
    public ResponseEntity<NoDataApiResponse> logout(
            @AuthenticationPrincipal String uid) {

        userService.logout(uid);
        return ResponseEntity.ok(NoDataApiResponse.success("로그아웃 완료"));
    }
}
