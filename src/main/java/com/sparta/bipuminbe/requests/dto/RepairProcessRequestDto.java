package com.sparta.bipuminbe.requests.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Builder
public class RepairProcessRequestDto {
    @NotNull
    private Long requestId;
    @NotNull
    private String acceptResult;
    private String comment;
}
