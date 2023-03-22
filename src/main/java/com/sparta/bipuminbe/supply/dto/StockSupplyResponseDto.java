package com.sparta.bipuminbe.supply.dto;

import com.sparta.bipuminbe.common.entity.Supply;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockSupplyResponseDto {
    private Long supplyId;
    private String modelName;
    private String serialNum;

    public static StockSupplyResponseDto of(Supply supply) {
        return StockSupplyResponseDto.builder()
                .supplyId(supply.getSupplyId())
                .modelName(supply.getModelName())
                .serialNum(supply.getSerialNum())
                .build();
    }
}
