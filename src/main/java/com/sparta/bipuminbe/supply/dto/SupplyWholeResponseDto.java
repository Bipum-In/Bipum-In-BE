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
    private List<SupplyRepairHistoryResponseDto> supplyRepairHistory = new ArrayList<>();


    SupplyWholeResponseDto(SupplyDetailResponseDto supplyDetail, List<SupplyHistoryResponseDto> supplyHistory, List<SupplyRepairHistoryResponseDto> supplyRepairHistory){
        this.supplyDetail =supplyDetail;
        this.supplyHistory=supplyHistory;
        this.supplyRepairHistory=supplyRepairHistory;
    }

    public static SupplyWholeResponseDto of(SupplyDetailResponseDto supplyDetail, List<SupplyHistoryResponseDto> supplyHistory, List<SupplyRepairHistoryResponseDto> supplyRepairHistory){
        return new SupplyWholeResponseDto(supplyDetail, supplyHistory, supplyRepairHistory);
    }
}
