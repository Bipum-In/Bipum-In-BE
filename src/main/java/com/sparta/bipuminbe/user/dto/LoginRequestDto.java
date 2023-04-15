package com.sparta.bipuminbe.user.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
public class LoginRequestDto {
    @NotNull
    private Long departmentId;
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z가-힣 ]*${4,30}")
    private String empName;
    @NotBlank
    private String phone;
    @NotBlank
    @Pattern(regexp = "^[0-9]*${6}")
    private String password;
}
