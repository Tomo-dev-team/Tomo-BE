package com.example.tomo.Promise;

import com.example.tomo.Users.dtos.ResponsePostUniformDto;
import com.example.tomo.global.ReponseType.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Promise API", description = "약속 관련 API")
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PromiseController {

    private final PromiseService promiseService;

    @Operation(
            summary = "약속 추가",
            description = "새로운 약속을 생성합니다",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "약속 생성 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "모임이 존재하지 않음"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 존재하는 약속")
            }
    )
    @PostMapping("/promises")
    public ResponseEntity<ResponsePostUniformDto> addPromise(
            @Valid @RequestBody addPromiseRequestDTO dto) {

        return ResponseEntity.ok(promiseService.addPromise(dto));
    }

    @Operation(
            summary = "약속 단건 조회",
            description = "약속 이름으로 특정 약속을 조회합니다",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "약속 조회 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 약속")
            }
    )
    @GetMapping("/promises")
    public ResponseEntity<ApiResponse<ResponseGetPromiseDto>> getPromise(
            @Parameter(description = "조회할 약속 이름", required = true)
            @RequestParam String promiseName) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        promiseService.getPromise(promiseName),
                        "약속 조회 성공"
                )
        );
    }

    @Operation(
            summary = "모임의 모든 약속 조회",
            description = "모임 이름으로 해당 모임의 모든 약속을 조회합니다",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "약속 리스트 조회 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 모임이 존재하지 않음")
            }
    )
    @GetMapping("/moims/promises")
    public ResponseEntity<ApiResponse<List<ResponseGetPromiseDto>>> getAllPromisesInMoims(
            @Parameter(description = "모임 이름", required = true)
            @RequestParam String moimName) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        promiseService.getAllPromise(moimName),
                        "성공"
                )
        );
    }

    @GetMapping("/promises/all")
    public ResponseEntity<ApiResponse<List<ResponseGetPromiseDto>>> getAllPromises(
            @AuthenticationPrincipal String uid
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        promiseService.getAllPromiseByUserId(uid),
                        "성공"
                )
        );
    }

    @GetMapping("/promises/upcomming")
    public ResponseEntity<ApiResponse<List<ResponseGetPromiseDto>>> getAllUpcommingPromises(
            @AuthenticationPrincipal String uid

    ){
        return ResponseEntity.ok(
                ApiResponse.success(
                        promiseService.getAllUpcomingPromiseByUserId(uid),
                        "성공"

                )
        );

    }

}
