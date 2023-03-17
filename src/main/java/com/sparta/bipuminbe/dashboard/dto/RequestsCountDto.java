package com.sparta.bipuminbe.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class RequestsCountDto {
    private Long supplyRequests;
    private Long returnRequests;
    private Long repairRequests;
    private Long inRepair;
    private LocalDateTime supplyModifiedAt;
    private LocalDateTime returnModifiedAt;
    private LocalDateTime repairModifiedAt;
    private LocalDateTime inRepairModifiedAt;

    public static RequestsCountDto of(Long supplyRequests, Long returnRequests, Long repairRequests, Long inRepair,
LocalDateTime supplyModifiedAt, LocalDateTime returnModifiedAt,LocalDateTime repairModifiedAt, LocalDateTime inRepairModifiedAt) {

        return RequestsCountDto.builder()
                .supplyRequests(supplyRequests)
                .returnRequests(returnRequests)
                .repairRequests(repairRequests)
                .inRepair(inRepair)
                .supplyModifiedAt(supplyModifiedAt)
                .returnModifiedAt(returnModifiedAt)
                .repairModifiedAt(repairModifiedAt)
                .inRepairModifiedAt(inRepairModifiedAt)
                .build();
    }

}
