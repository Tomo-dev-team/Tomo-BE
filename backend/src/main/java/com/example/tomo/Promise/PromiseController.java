package com.example.tomo.Promise;


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

@Tag(name = "Promise API", description = "약속 관련 API")
@RestController
@RequestMapping("/public/promises")
@RequiredArgsConstructor
public class PromiseController {

    private final PromiseService promiseService;
    private final PromiseResponseAssembler promiseResponseAssembler;

    /* =====================
       약속 생성
       ===================== */
    @Operation(
            summary = "약속 생성",
            description = "모임에 속한 모든 멤버를 대상으로 새로운 약속을 생성합니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<PromiseResponseDto>> createPromise(
            @Valid @RequestBody addPromiseRequestDTO dto
    ) {
        Promise promise = promiseService.addPromise(dto);

        PromiseQueryResult result =
                promiseService.getPromise(promise.getPromiseName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        promiseResponseAssembler.toDto(result),
                        "약속이 생성되었습니다."
                ));
    }

    /* =====================
       약속 단일 조회
       ===================== */
    @Operation(
            summary = "약속 단일 조회",
            description = "약속 이름으로 특정 약속을 조회합니다."
    )
    @GetMapping("/{promiseName}")
    public ResponseEntity<ApiResponse<PromiseResponseDto>> getPromise(
            @Parameter(description = "약속 이름", required = true)
            @PathVariable String promiseName
    ) {
        PromiseQueryResult result = promiseService.getPromise(promiseName);

        return ResponseEntity.ok(
                ApiResponse.success(
                        promiseResponseAssembler.toDto(result),
                        "약속 조회 성공"
                )
        );
    }

    /* =====================
       모임의 모든 약속 조회
       ===================== */
    @Operation(
            summary = "모임의 모든 약속 조회",
            description = "특정 모임에 속한 모든 약속을 조회합니다."
    )
    @GetMapping("/moim")
    public ResponseEntity<ApiResponse<List<PromiseResponseDto>>> getAllPromisesInMoim(
            @Parameter(description = "모임 이름", required = true)
            @RequestParam String moimTitle
    ) {
        List<PromiseResponseDto> response =
                promiseService.getAllPromise(moimTitle)
                        .stream()
                        .map(promiseResponseAssembler::toDto)
                        .toList();

        return ResponseEntity.ok(
                ApiResponse.success(response, "모임의 약속 조회 성공")
        );
    }

    /* =====================
       나의 모든 약속 조회
       ===================== */
    @Operation(
            summary = "내 모든 약속 조회",
            description = "로그인한 사용자의 모든 약속을 조회합니다."
    )
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<PromiseResponseDto>>> getAllMyPromises(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String uid
    ) {
        List<PromiseResponseDto> response =
                promiseService.getAllPromiseByUserId(uid)
                        .stream()
                        .map(promiseResponseAssembler::toDto)
                        .toList();

        return ResponseEntity.ok(
                ApiResponse.success(response, "내 약속 조회 성공")
        );
    }

    /* =====================
       나의 남은 약속 조회
       ===================== */
    @Operation(
            summary = "내 남은 약속 조회",
            description = "현재 시점 이후의 약속만 조회합니다."
    )
    @GetMapping("/my/upcoming")
    public ResponseEntity<ApiResponse<List<PromiseResponseDto>>> getUpcomingPromises(
            @Parameter(hidden = true)
            @AuthenticationPrincipal String uid
    ) {
        List<PromiseResponseDto> response =
                promiseService.getAllUpcomingPromiseByUserId(uid)
                        .stream()
                        .map(promiseResponseAssembler::toDto)
                        .toList();

        return ResponseEntity.ok(
                ApiResponse.success(response, "남은 약속 조회 성공")
        );
    }
}
