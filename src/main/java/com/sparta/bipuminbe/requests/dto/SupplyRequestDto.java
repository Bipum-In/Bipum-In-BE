package com.sparta.bipuminbe.requests.dto;

import com.sparta.bipuminbe.common.entity.SupplyRequest;
import com.sparta.bipuminbe.common.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SupplyRequestDto {

    private String content;

    private String status;

    private Long supplyId;

    private User user;

    public static SupplyRequestDto of(SupplyRequest supplyRequest) {
        return SupplyRequestDto.builder()
                .content(supplyRequest.getContent())
                .status(supplyRequest.getStatus())
                .supplyId(supplyRequest.getSupply().getSupplyId())
                .build();
    }
}