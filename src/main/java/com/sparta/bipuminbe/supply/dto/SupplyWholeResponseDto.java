package com.sparta.bipuminbe.supply.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class SupplyWholeResponseDto {

    private List<SupplyDetailResponseDto> supplyDetail = new ArrayList<>();

    private List<SupplyHistoryResponseDto> supplyHistory = new ArrayList<>();

}
