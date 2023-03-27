package com.sparta.bipuminbe.supply.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class SupplyWholeResponseDto {

    private SupplyDetailResponseDto supplyDetail;

    private Page<SupplyHistoryResponseDto> supplyUserHistory;
    private Page<SupplyHistoryResponseDto> supplyRepairHistory;


//    SupplyWholeResponseDto(SupplyDetailResponseDto supplyDetail, Page<SupplyHistoryResponseDto> supplyUserHistory, Page<SupplyHistoryResponseDto> supplyRepairHistory) {
//        this.supplyDetail = supplyDetail;
//        this.supplyUserHistory = supplyUserHistory;
//        this.supplyRepairHistory = supplyRepairHistory;
//    }

    public static SupplyWholeResponseDto of(SupplyDetailResponseDto supplyDetail, Page<SupplyHistoryResponseDto> supplyUserHistory, Page<SupplyHistoryResponseDto> supplyRepairHistory) {
        return SupplyWholeResponseDto.builder()
                .supplyDetail(supplyDetail)
                .supplyUserHistory(supplyUserHistory)
                .supplyRepairHistory(supplyRepairHistory)
                .build();
    }
}
