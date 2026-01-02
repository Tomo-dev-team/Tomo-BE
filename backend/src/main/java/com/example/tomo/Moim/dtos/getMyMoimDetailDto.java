package com.example.tomo.Moim.dtos;

import com.example.tomo.Moim.Moim;
import com.example.tomo.global.Embedded.Location;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class getMyMoimDetailDto {

    private String title;
    private String description;
    private Boolean isPublic;
    private List<String> emails;
    private Location location;

    public void from(Moim moim){
        this.title = moim.getTitle();
        this.description = moim.getDescription();
        this.isPublic = moim.getIsPublic();
        this.emails = new ArrayList<>();
        this.location = moim.getLocation();
    }
}
