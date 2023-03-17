package com.sparta.bipuminbe.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
@Getter
public class RequestsCountDto {
    private Map<String, Long> countMap;
    private Map<String, LocalDateTime> modifiedAtMap;

    public static RequestsCountDto of(Map<String, Long> countMap,
                                      Map<String, LocalDateTime> modifiedAtMap) {
        return RequestsCountDto.builder()
                .countMap(countMap)
                .modifiedAtMap(modifiedAtMap)
                .build();
    }
}
