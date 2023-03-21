package com.sparta.bipuminbe.requests.dto;

import lombok.Getter;

@Getter
public class SupplyProcessResponseDto {
    private Long requestId;
    private Long supplyId;
    private String acceptResult;
    private String comment;
}
