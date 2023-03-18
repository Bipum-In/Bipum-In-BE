package com.sparta.bipuminbe.supply.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SupplyRequestDto {
    private Long categoryId;
    private String modelName;
    private String serialNum;
    private LocalDateTime createdAt;
    private Long partnersId;
    private Long userId;
    private String image;
}
