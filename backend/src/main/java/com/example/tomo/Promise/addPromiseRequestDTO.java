package com.example.tomo.Promise;

import com.example.tomo.global.Embedded.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class addPromiseRequestDTO {

    @NotBlank(message = "모임명이 누락되었습니다")
    private String title; // 모임명, 존재하지 않는 모임에서 약속을 생성하는 것을 불가능하게 설정

    @NotBlank(message = "약속명이 누락되었습니다")
    private String promiseName; // 약속명

    @NotNull(message = "약속날짜가 누락되었습니다")
    private LocalDate promiseDate; // 약속 날짜

    @NotNull(message = "약속 시간이 누락되었습니다")
    private LocalTime promiseTime; // 약속 시간

    @NotBlank(message = "약속 장소가 누락되었습니다")
    private String place; // 약속 장소

    @NotNull(message = "위치 정보가 누락되었습니다." )
    private Location location;

}
