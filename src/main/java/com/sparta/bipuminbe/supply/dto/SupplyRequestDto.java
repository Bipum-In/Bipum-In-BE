package com.sparta.bipuminbe.supply.dto;

import com.sparta.bipuminbe.common.enums.LargeCategory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SupplyRequestDto {
    private LargeCategory largeCategory;
    private String categoryName;
    private String modelName;
    private String serialNum;
    private LocalDateTime createdAt;
    private Long partnersId;
    private Long userId;
    private String image;
}
