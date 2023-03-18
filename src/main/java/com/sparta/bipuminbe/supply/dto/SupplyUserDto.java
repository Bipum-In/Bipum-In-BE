package com.sparta.bipuminbe.supply.dto;

import com.sparta.bipuminbe.common.entity.Supply;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SupplyUserDto {

    private Long supplyId;
    private String modelName;
    private String serialNum;

    public SupplyUserDto(Supply supply){
        this.supplyId = supply.getSupplyId();
        this.modelName = supply.getModelName();
        this.serialNum = supply.getSerialNum();
    }

    public static SupplyUserDto of (Supply supply) {
        return new SupplyUserDto(supply);
    }
}
