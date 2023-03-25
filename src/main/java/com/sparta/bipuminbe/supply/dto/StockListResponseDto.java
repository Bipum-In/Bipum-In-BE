package com.sparta.bipuminbe.supply.dto;

import com.sparta.bipuminbe.common.entity.Supply;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StockListResponseDto {
    private Long supplyId;
    private String modelName;
    private String image;
    private LocalDateTime createdAt;

    public static StockListResponseDto of(Supply supply) {
        return StockListResponseDto.builder()
                .supplyId(supply.getSupplyId())
                .modelName(supply.getModelName())
                .image(supply.getImage())
                .createdAt(supply.getCreatedAt())
                .build();
    }
}
