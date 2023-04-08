package com.sparta.bipuminbe.dashboard.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class SearchTotalDto {
    private List<SupplySearchDto> supplySearchDtoList;
    private List<RequestsSearchDto> requestsSearchDtoList;
}
