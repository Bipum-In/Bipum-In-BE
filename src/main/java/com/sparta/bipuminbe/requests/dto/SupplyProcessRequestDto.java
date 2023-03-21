package com.sparta.bipuminbe.requests.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class SupplyProcessRequestDto {
    @NotNull
    private Long requestId;
    @NotNull
    private String acceptResult;
    private Long supplyId;
    private String comment;
}
