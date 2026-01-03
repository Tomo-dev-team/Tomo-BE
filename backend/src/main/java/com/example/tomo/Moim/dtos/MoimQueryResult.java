package com.example.tomo.Moim.dtos;

import com.example.tomo.Moim.Moim;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MoimQueryResult {
    private Moim moim;
    private List<String> emails;
    private boolean leader;
}
