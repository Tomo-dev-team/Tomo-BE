package com.example.tomo.Moim.dtos;

import com.example.tomo.global.Embedded.Location;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class addMoimRequestDto {

    @NotBlank(message = "모임명이 누락되었습니다.")
    private String title;

    @NotBlank(message = "모임 설명이 누락되었습니다.")
    private String description;

    @NotNull(message = "모임 공개 여부가 누락되었습니다.")
    private Boolean isPublic;

    @NotEmpty(message ="이메일 목록은 비어 있을 수 없습니다")
    private List<
            @NotBlank(message = "이메일은 공백일 수 없습니다.")
            @Email(message = "이메일 형식이 올바르지 않습니다.")String> emails; // 사용자 이름 , 이메일로

    @NotNull(message = "위치 정보가 누락되었습니다")
    private Location location;
}
