package com.sparta.bipuminbe.requests.dto;

import com.sparta.bipuminbe.common.enums.AcceptResult;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class RequestsProcessRequestDto {
    @NotNull
    private AcceptResult acceptResult;
    private Long supplyId;
    private String comment;
}
