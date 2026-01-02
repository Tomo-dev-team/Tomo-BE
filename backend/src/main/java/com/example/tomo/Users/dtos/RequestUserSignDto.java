package com.example.tomo.Users.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestUserSignDto {

    @NotBlank(message = "uid가 누락되었습니다.")
    private String uuid;

    @Email (message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "email이 누락되었습니다")
    private String email;

    @NotBlank(message = "사용자가 누락되었습니다.")
    private String username;

}
