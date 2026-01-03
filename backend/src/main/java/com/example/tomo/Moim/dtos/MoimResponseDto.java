package com.example.tomo.Moim.dtos;

import com.example.tomo.Moim.Moim;
import com.example.tomo.global.Embedded.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MoimResponseDto {

    private Long moimId;
    private String title;
    private String description;
    private List<String> emails;
    private Location location;
    private Boolean leader;
    private LocalDate createdAt;
    private Boolean isPublic;

}
