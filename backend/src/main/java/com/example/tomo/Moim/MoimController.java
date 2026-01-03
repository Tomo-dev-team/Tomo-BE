package com.example.tomo.Moim;

import com.example.tomo.Moim.dtos.MoimQueryResult;
import com.example.tomo.Moim.dtos.MoimResponseDto;
import com.example.tomo.Moim.dtos.AddMoimRequestDto;
import com.example.tomo.global.ReponseType.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Moim API", description = "모임 생성, 조회, 삭제 API")
@RestController
@RequestMapping("/public/moims")
@RequiredArgsConstructor
public class MoimController {

    private final MoimService moimService;
    private final MoimResponseAssembler moimResponseAssembler;

    /* =====================
       모임 생성
       ===================== */
    @PostMapping
    public ResponseEntity<ApiResponse<MoimResponseDto>> createMoim(
            @Valid @RequestBody AddMoimRequestDto dto,
            @AuthenticationPrincipal String uid
    ) {
        Moim moim = moimService.createMoim(
                uid,
                dto.getTitle(),
                dto.getDescription(),
                dto.getIsPublic(),
                dto.getLocation(),
                dto.getEmails()
        );

        MoimQueryResult result = moimService.getMoim(moim.getId(), uid);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        moimResponseAssembler.toDto(
                                result.getMoim(),
                                result.getEmails(),
                                result.isLeader()
                        ),
                        "모임이 생성되었습니다."
                ));
    }

    /* =====================
       모임 단일 조회
       ===================== */
    @Operation(
            summary = "모임 단일 조회",
            description = "모임 ID를 통해 모임 상세 정보를 조회합니다."
    )
    @GetMapping("/{moimId}")
    public ResponseEntity<ApiResponse<MoimResponseDto>> getMoim(
            @PathVariable Long moimId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal String uid
    ) {
        MoimQueryResult result = moimService.getMoim(moimId, uid);

        return ResponseEntity.ok(
                ApiResponse.success(
                        moimResponseAssembler.toDto(
                                result.getMoim(),
                                result.getEmails(),
                                result.isLeader()
                        ),
                        "모임 조회 성공"
                )
        );
    }

    /* =====================
       내가 속한 모임 목록 조회
       ===================== */
    @Operation(
            summary = "내가 속한 모임 목록 조회",
            description = "로그인한 사용자가 참가 중인 모든 모임을 조회합니다."
    )
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<MoimResponseDto>>> getMyMoims(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String uid
    ) {
        List<MoimResponseDto> response = moimService.getMyMoims(uid)
                .stream()
                .map(result ->
                        moimResponseAssembler.toDto(
                                result.getMoim(),
                                result.getEmails(),
                                result.isLeader()
                        )
                )
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success(response, "내 모임 목록 조회 성공")
        );
    }

    /* =====================
       공개 모임 목록 조회
       ===================== */
    @Operation(
            summary = "공개 모임 목록 조회",
            description = "전체 공개 모임 목록을 조회합니다."
    )
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<MoimResponseDto>>> getPublicMoims() {

        List<MoimResponseDto> response = moimService.getPublicMoims()
                .stream()
                .map(result ->
                        moimResponseAssembler.toDto(
                                result.getMoim(),
                                result.getEmails(),
                                false
                        )
                )
                .toList();

        return ResponseEntity.ok(
                ApiResponse.success(response, "공개 모임 조회 성공")
        );
    }

    /* =====================
       모임 삭제
       ===================== */
    @Operation(
            summary = "모임 삭제",
            description = "모임 리더만 모임을 삭제할 수 있습니다."
    )
    @DeleteMapping("/{moimId}")
    public ResponseEntity<ApiResponse<Void>> deleteMoim(
            @PathVariable Long moimId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal String uid
    ) {
        moimService.deleteMoim(moimId, uid);

        return ResponseEntity.ok(
                ApiResponse.success(null, "모임이 삭제되었습니다.")
        );
    }
}
