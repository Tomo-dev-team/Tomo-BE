package com.example.tomo.Promise;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PromiseQueryResult {

    private Promise promise;
    private Long moimId;
    private String moimTitle;
}
