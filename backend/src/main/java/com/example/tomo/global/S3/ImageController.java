package com.example.tomo.global.S3;

import com.example.tomo.global.ReponseType.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final S3UploadService s3UploadService;

    @PostMapping
    public ApiResponse<String> upload(
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal String uid
    ) throws IOException {

        String imageUrl = s3UploadService.upload(image, uid);

        return ApiResponse.success(imageUrl, "이미지 업로드 성공");
    }


}

