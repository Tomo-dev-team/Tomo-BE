package com.example.tomo.Moim;

import com.example.tomo.Moim.dtos.*;
import com.example.tomo.global.ReponseType.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name = "Moim API", description = "모임 관련 API")
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class MoimController {

    private final MoimService moimService;

    @Operation(summary = "모임 생성")
    @PostMapping("/moims")
    public ResponseEntity<ApiResponse<addMoimResponseDto>> addMoim(
            @Valid @RequestBody addMoimRequestDto dto,
            @AuthenticationPrincipal String uid
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        moimService.addMoim(uid, dto),
                        "모임이 생성되었습니다."
                ));
    }

    @Operation(summary = "모임 상세 조회")
    @GetMapping("/moims/{moim_id}")
    public ResponseEntity<ApiResponse<getDetailMoimDto>> getMoimDetail(
            @PathVariable("moim_id") long moimId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        moimService.getMoimDetail(moimId),
                        "성공"
                )
        );
    }

    @Operation(summary = "내 모임 리스트 조회")
    @GetMapping("/moims/list")
    public ResponseEntity<ApiResponse<List<getMoimResponseDto>>> getMyMoims(
            @AuthenticationPrincipal String uid
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        moimService.getMoimList(uid),
                        "모임 조회 성공"
                )
        );
    }

    @Operation(summary = "모임 삭제")
    @DeleteMapping("/moims/{moim_id}")
    public ResponseEntity<ApiResponse<Void>> deleteMoim(
            @PathVariable("moim_id") long moimId,
            @AuthenticationPrincipal String uid
    ) {
        moimService.deleteMoim(moimId, uid);
        return ResponseEntity.ok(
                ApiResponse.success(null, "모임이 삭제되었습니다.")
        );
    }

    @Operation(summary ="사용자가 참가하는 모든 모임 조회")
    @GetMapping("/moims/my")
    public ResponseEntity<ApiResponse<List<getMyMoimDetailDto>>> getAllMyMoims(
            @AuthenticationPrincipal String uid
    ){
        return ResponseEntity.ok(
                ApiResponse.success(
                        moimService.getMyAndPublicMoim(uid),
                        "성공"

                )
        );
    }

    @GetMapping("/moims/all")
    public ResponseEntity<ApiResponse<List<getMyMoimDetailDto>>> getAllMoims(){
        return ResponseEntity.ok(
                ApiResponse.success(
                        moimService.getPublicMoim(),
                        "성공"
                )
        );
    }


}
