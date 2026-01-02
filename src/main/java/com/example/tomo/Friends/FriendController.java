package com.example.tomo.Friends;


import com.example.tomo.Friends.dtos.ResponseFriendDetailDto;
import com.example.tomo.Users.UserService;
import com.example.tomo.Users.dtos.ResponsePostUniformDto;
import com.example.tomo.Users.dtos.getFriendResponseDto;
import com.example.tomo.global.ReponseType.ApiResponse;
import com.example.tomo.global.ReponseType.NoDataApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Friend API", description = "친구 관련 API")
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;
    private final UserService userService;

    @Operation(summary = "친구 추가", description = "이메일을 이용하여 친구를 추가합니다.")
    @PostMapping("/friends")
    public ResponseEntity<ResponsePostUniformDto> addFriendsUsingEmail(
            @AuthenticationPrincipal String uid,
            @RequestParam String query
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.addFriends(uid, query));
    }

    @Operation(summary = "친구 조회", description = "이메일로 친구 정보를 조회합니다.")
    @GetMapping("/friends")
    public ResponseEntity<ApiResponse<getFriendResponseDto>> getFriendsUsingEmail(
            @RequestParam String query
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(userService.getUserInfo(query), "친구 조회 완료")
        );
    }

    @Operation(summary = "친구 상세 조회")
    @GetMapping("/friends/detail")
    public ResponseEntity<ApiResponse<ResponseFriendDetailDto>> getFriendsDetailUsingEmail(
            @AuthenticationPrincipal String uid,
            @RequestParam String query
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(friendService.getFriendDetail(uid, query), "조회 성공")
        );
    }

    @Operation(summary = "친구 목록 조회")
    @GetMapping("/friends/list")
    public ResponseEntity<ApiResponse<List<ResponseFriendDetailDto>>> getFriendDetails(
            @AuthenticationPrincipal String uid
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(friendService.getFriends(uid), "성공")
        );
    }

    @Operation(summary = "친구 삭제")
    @DeleteMapping("/friends")
    public ResponseEntity<NoDataApiResponse> removeFriend(
            @AuthenticationPrincipal String uid,
            @RequestParam String friendEmail
    ) {
        friendService.removeFriend(uid, friendEmail);
        return ResponseEntity.ok(
                NoDataApiResponse.success("친구가 삭제되었습니다.")
        );
    }
}
