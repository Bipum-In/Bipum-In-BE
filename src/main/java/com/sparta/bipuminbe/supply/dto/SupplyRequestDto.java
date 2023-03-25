package com.sparta.bipuminbe.supply.dto;

import com.sparta.bipuminbe.common.enums.LargeCategory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
public class SupplyRequestDto {
    private LargeCategory largeCategory;
    private String categoryName;
    private String modelName;
    private String serialNum;
    private Long partnersId;
    private Long userId;
    private String image;
    private MultipartFile multipartFile;
}
