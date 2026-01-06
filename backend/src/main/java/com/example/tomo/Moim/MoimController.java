package com.example.tomo.Moim;

import com.example.tomo.Moim.dtos.MoimQueryResult;
import com.example.tomo.Moim.dtos.MoimResponseDto;
import com.example.tomo.Moim.dtos.AddMoimRequestDto;
import com.example.tomo.global.ReponseType.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "Moim API", description = "모임 생성, 조회, 삭제 API")
@RestController
@RequestMapping("/public/moims")
@RequiredArgsConstructor
public class MoimController {

    private final MoimService moimService;
    private final MoimResponseAssembler moimResponseAssembler;
    private final ObjectMapper objectMapper;


    /* =====================
       모임 생성
       ===================== */
    /* =====================
   모임 생성
   ===================== */

    @Operation(
            summary = "모임 생성 (이미지 없음)",
            description = """
        대표 이미지 없이 모임을 생성합니다.

        Content-Type: application/json
        """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "모임 생성 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MoimResponseDto.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<MoimResponseDto>> createMoim(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "모임 생성 요청 정보",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AddMoimRequestDto.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                  "title": "개발 스터디",
                                  "description": "Spring 백엔드 스터디",
                                  "isPublic": true,
                                  "emails": ["test@test.com"],
                                  "location": {
                                    "latitude": 37.5,
                                    "longitude": 127.0
                                  }
                                }
                                """
                            )
                    )
            )
            @RequestBody AddMoimRequestDto dto,
            @AuthenticationPrincipal String uid
    ) {

        Moim moim = moimService.createMoim(
                uid,
                dto.getTitle(),
                dto.getDescription(),
                dto.getIsPublic(),
                dto.getLocation(),
                dto.getEmails(),
                null
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


    @Operation(
            summary = "모임 생성 (대표 이미지 선택 가능)",
            description = """
        모임을 생성하면서 대표 이미지를 함께 업로드합니다.

        Content-Type: multipart/form-data

        - request: 모임 생성 정보(JSON 문자열)
        - image: JPG 이미지 파일 (선택)
        """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "모임 생성 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MoimResponseDto.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MoimResponseDto>> createMoimWithImage(

            @Parameter(
                    description = "모임 생성 정보 (JSON 문자열)",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AddMoimRequestDto.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                  "title": "개발 스터디",
                                  "description": "Spring 백엔드 스터디",
                                  "isPublic": true,
                                  "emails": ["test@test.com"],
                                  "location": {
                                    "latitude": 37.5,
                                    "longitude": 127.0
                                  }
                                }
                                """
                            )
                    )
            )
            @RequestPart("request") String requestJson,

            @Parameter(
                    description = "모임 대표 이미지 (JPG)",
                    required = false,
                    content = @Content(
                            mediaType = MediaType.IMAGE_JPEG_VALUE
                    )
            )
            @RequestPart(value = "image", required = false) MultipartFile image,

            @AuthenticationPrincipal String uid
    ) throws IOException {

        // 1️⃣ JSON 문자열 → DTO 변환
        AddMoimRequestDto dto =
                objectMapper.readValue(requestJson, AddMoimRequestDto.class);

        // 2️⃣ 모임 생성
        Moim moim = moimService.createMoim(
                uid,
                dto.getTitle(),
                dto.getDescription(),
                dto.getIsPublic(),
                dto.getLocation(),
                dto.getEmails(),
                image
        );

        // 3️⃣ 응답 조립
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
