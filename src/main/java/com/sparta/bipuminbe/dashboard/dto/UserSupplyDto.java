package com.sparta.bipuminbe.dashboard.dto;

import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.enums.SupplyStatusEnum;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSupplyDto {
    private Long supplyId;
    private String image;
    private String supplyName;
    private SupplyStatusEnum status;

    public static UserSupplyDto of(Supply supply) {
        return builder()
                .supplyId(supply.getSupplyId())
                .image(supply.getImage())
                .supplyName(supply.getModelName())
                .status(supply.getStatus())
                .build();
    }
}
