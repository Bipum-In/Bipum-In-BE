package com.sparta.bipuminbe.user.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
public class UserUpdateRequestDto {
    @Pattern(regexp = "^[a-zA-Zㄱ-ㅎ가-힣 ]*${4,30}")
    private String empName;
    @NotNull
    @NotBlank
    private Long deptId;
    @NotNull
    @NotBlank
    private String phone;
    @NotNull
    @NotBlank
    private Boolean alarm;
}
