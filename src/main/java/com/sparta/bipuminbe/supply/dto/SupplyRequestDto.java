package com.sparta.bipuminbe.supply.dto;

import com.sparta.bipuminbe.common.enums.LargeCategory;
import com.sparta.bipuminbe.common.enums.UseType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Getter
@Setter
public class SupplyRequestDto {
    @NotNull
    private LargeCategory largeCategory;
    @NotBlank
    @Pattern(regexp = "[^ㄱ-ㅎㅏ-ㅣ]{1,30}$", message = "한글 자음/모음이 아닌 30자 이내")
    private String categoryName;
    @NotBlank
    @Pattern(regexp = "[^ㄱ-ㅎㅏ-ㅣ]{1,30}$", message = "한글 자음/모음이 아닌 30자 이내")
    private String modelName;
    @NotBlank
    @Pattern(regexp = "[^ㄱ-ㅎㅏ-ㅣ]{1,50}$", message = "한글 자음/모음이 아닌 50자 이내")
    private String serialNum;
    private Long partnersId;
    private UseType useType;
    private Long userId;
    private Long deptId;
    private String image;
    private MultipartFile multipartFile;
}
