package com.example.tomo.global.S3;

import com.example.tomo.Users.User;
import com.example.tomo.Users.UserErrorCode;
import com.example.tomo.Users.UserException;
import com.example.tomo.Users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final S3Client s3Client;
    private final UserRepository userRepository;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    public String upload(MultipartFile file, String uid) throws IOException {
        validate(file);
        User user = userRepository.findByFirebaseId(uid)
                .orElseThrow(()-> new UserException(UserErrorCode.USER_NOT_FOUND));

        Long userId = user.getId();

        // 1. 파일명 생성 (중복 방지)
        String key = "images/user/" + userId + "/" + UUID.randomUUID() + ".jpg";


        // 2. S3 업로드 요청
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );

        // 3. 접근 가능한 URL 반환
        return "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/" + key;
    }

    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    private void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일입니다");
        }

        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("파일 크기는 5MB 이하만 허용됩니다");
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("허용되지 않은 이미지 타입입니다");
        }
    }

}


