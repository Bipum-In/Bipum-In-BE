package com.sparta.bipuminbe.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class UserUpdateRequestDto {
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z가-힣 ]*${4,30}")
    private String empName;
    @Pattern(regexp = "^[0-9]*${6}")
    private String password;
    @NotNull
    private Long deptId;
    @NotBlank
    private String phone;
    @NotNull
    private Boolean alarm;
    private String image;
    private MultipartFile multipartFile;
}
