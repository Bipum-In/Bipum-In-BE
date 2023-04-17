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
    @Pattern(regexp = "[^ㄱ-ㅎㅏ-ㅣ]{1,30}$", message = "한글 자음/모음이 아닌 30자 이내")
    private String empName;
    @NotNull
    private Long deptId;
    @NotBlank
    private String phone;
    @NotNull
    private Boolean alarm;
    private String image;
    private MultipartFile multipartFile;
}
