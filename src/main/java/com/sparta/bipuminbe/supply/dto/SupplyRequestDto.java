package com.sparta.bipuminbe.supply.dto;

import com.sparta.bipuminbe.common.enums.LargeCategory;
import com.sparta.bipuminbe.common.enums.UseType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;


@Getter
@Setter
public class SupplyRequestDto {
    @NotNull
    private LargeCategory largeCategory;
    @NotBlank
    private String categoryName;
    @NotBlank
    @Pattern(regexp = "[ㄱ-ㅎㅏ-ㅣ]*${4,30}")
    private String modelName;
    @NotBlank
    @Pattern(regexp = "[ㄱ-ㅎㅏ-ㅣ]*${4,50}")
    private String serialNum;
    private Long partnersId;
    private UseType useType;
    private Long userId;
    private Long deptId;
    private String image;
    private MultipartFile multipartFile;
}
