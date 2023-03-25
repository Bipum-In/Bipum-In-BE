package com.sparta.bipuminbe.supply.dto;

import com.sparta.bipuminbe.common.enums.LargeCategory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Setter
public class SupplyRequestDto {
    @NotNull
    private LargeCategory largeCategory;
    @NotNull
    @NotBlank
    private String categoryName;
    @NotNull
    @NotBlank
    private String modelName;
    @NotNull
    @NotBlank
    private String serialNum;
    private Long partnersId;
    private Long userId;
    private String image;
    private MultipartFile multipartFile;
}
