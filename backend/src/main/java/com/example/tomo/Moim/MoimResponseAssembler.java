package com.example.tomo.Moim;

import com.example.tomo.Moim.dtos.MoimResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MoimResponseAssembler {

    public MoimResponseDto toDto(Moim moim, List<String> emails, boolean leader) {
        return new MoimResponseDto(
                moim.getId(),
                moim.getTitle(),
                moim.getDescription(),
                emails,
                moim.getLocation(),
                leader,
                moim.getCreatedAt(),
                moim.getIsPublic()
        );
    }
}
