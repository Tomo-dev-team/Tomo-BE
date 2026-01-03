package com.example.tomo.Promise;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.example.tomo.global.Embedded.Location;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class PromiseResponseDto {

    private Long promiseId;
    private String promiseName;
    private LocalDate promiseDate;
    private LocalTime promiseTime;
    private String place;
    private Location location;
    private Long moimId;
    private String moimTitle;
}
