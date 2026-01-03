package com.example.tomo.Promise;

import org.springframework.stereotype.Component;

@Component
public class PromiseResponseAssembler {

    public PromiseResponseDto toDto(PromiseQueryResult result) {
        Promise p = result.getPromise();

        return new PromiseResponseDto(
                p.getId(),
                p.getPromiseName(),
                p.getPromiseDate(),
                p.getPromiseTime(),
                p.getPlace(),
                p.getLocation(),
                p.getMoim().getId(),
                p.getMoim().getTitle()
        );
    }

}
