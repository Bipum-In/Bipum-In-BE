package com.sparta.bipuminbe.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AdminMainResponseDto {

    private List<SupplyCountDto> supplyCountDtos;

    private RequestsCountDto requestsCountDto;

    public static AdminMainResponseDto of(
            List<SupplyCountDto> supplyCountDtos, RequestsCountDto requestsCountDto) {

        return AdminMainResponseDto.builder()
                .supplyCountDtos(supplyCountDtos)
                .requestsCountDto(requestsCountDto)
                .build();
    }
}
