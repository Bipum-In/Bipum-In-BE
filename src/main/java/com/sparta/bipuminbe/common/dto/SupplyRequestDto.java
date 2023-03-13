package com.sparta.bipuminbe.common.dto;

import com.sparta.bipuminbe.common.entity.SupplyRequest;
import lombok.Getter;

@Getter
public class SupplyRequestDto {

    private String content;

    private String status;

    private SupplyRequestDto(SupplyRequest supplyRequest){
        this.content = supplyRequest.getContent();
        this.status = supplyRequest.getStatus();
    }
}
