package com.sparta.bipuminbe.supply.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class SupplyWholeResponseDto {

    private SupplyDetailResponseDto supplyDetail;

    private List<SupplyHistoryResponseDto> supplyHistory = new ArrayList<>();


    SupplyWholeResponseDto(SupplyDetailResponseDto supplyDetail, List<SupplyHistoryResponseDto> supplyHistory){
        this.supplyDetail =supplyDetail;
        this.supplyHistory=supplyHistory;
    }

    public static SupplyWholeResponseDto of(SupplyDetailResponseDto supplyDetail, List<SupplyHistoryResponseDto> supplyHistory){
        return new SupplyWholeResponseDto(supplyDetail, supplyHistory);
    }
}
