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
    @Pattern(regexp = "[^ㄱ-ㅎㅏ-ㅣ]{1,30}$", message = "한글 자/모음이 아닌 30자 이내")
    private String empName;
    @NotBlank
    private String phone;
    @NotBlank
    @Pattern(regexp = "^[0-9]*${6}", message = "6자리 숫자를 입력해 주세요.")
    private String password;
}
