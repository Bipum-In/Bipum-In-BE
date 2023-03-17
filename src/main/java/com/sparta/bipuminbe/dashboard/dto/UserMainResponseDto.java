package com.sparta.bipuminbe.dashboard.dto;

import com.sparta.bipuminbe.common.entity.Supply;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class UserMainResponseDto {
    private List<UserSupplyDto> userSupplyDtos;
    private Map<String, Long> userCountMap;

    public static UserMainResponseDto of(List<UserSupplyDto> userSupplyDtos, Map<String, Long> userCountMap) {
        return builder()
                .userSupplyDtos(userSupplyDtos)
                .userCountMap(userCountMap)
                .build();
    }
}
