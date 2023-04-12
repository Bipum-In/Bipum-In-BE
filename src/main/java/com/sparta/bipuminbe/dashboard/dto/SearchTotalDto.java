package com.sparta.bipuminbe.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchTotalDto {
    private List<SupplySearchDto> supplySearchDtoList;
    private List<RequestsSearchDto> requestsSearchDtoList;

    public static SearchTotalDto of(List<SupplySearchDto> supplySearchDtoList, List<RequestsSearchDto> requestsSearchDtoList) {
        return SearchTotalDto.builder()
                .supplySearchDtoList(supplySearchDtoList)
                .requestsSearchDtoList(requestsSearchDtoList)
                .build();
    }
}
