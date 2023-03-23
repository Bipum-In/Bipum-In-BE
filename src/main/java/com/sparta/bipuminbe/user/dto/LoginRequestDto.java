package com.sparta.bipuminbe.user.dto;

import lombok.Getter;

import javax.validation.constraints.Pattern;

@Getter
public class LoginRequestDto {
    private Long departmentId;

    @Pattern(regexp = "^[a-zA-Zㄱ-ㅎ가-힣 ]*${4,30}")
    private String empName;

    private String phone;
}
