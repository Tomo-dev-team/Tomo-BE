package com.example.tomo.Promise;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseGetPromiseDto {

    private String promiseName;
    private LocalDate promiseDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Seoul")
    private LocalTime promiseTime;
    private String place;


    public static ResponseGetPromiseDto from(Promise promise) {
        return new ResponseGetPromiseDto(
                promise.getPromiseName(),
                promise.getPromiseDate(),
                promise.getPromiseTime(),
                promise.getPlace()
        );
    }

}
