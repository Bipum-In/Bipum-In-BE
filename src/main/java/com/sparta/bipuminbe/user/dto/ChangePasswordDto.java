package com.sparta.bipuminbe.user.dto;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
public class ChangePasswordDto {
    @NotEmpty
    @Pattern(regexp = "^[0-9]*${6}")
    private String password;
}
