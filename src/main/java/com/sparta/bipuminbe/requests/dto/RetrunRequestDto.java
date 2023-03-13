package com.sparta.bipuminbe.requests.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.ReturnRequest;
import com.sparta.bipuminbe.common.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RetrunRequestDto<T> {


    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    public static <T> ResponseDto<T> success(T data) {

        return new ResponseDto<>(200, data);
    }
    @Getter
    @AllArgsConstructor
    public static class Error {
        private final String errorMessage;
    }

    private String content;

    private String status;

    private Long supplyId;

    private User user;

    public static RetrunRequestDto of(ReturnRequest returnRequest) {
        return RetrunRequestDto.builder()
                .content(returnRequest.getContent())
                .status(returnRequest.getStatus())
                .supplyId(returnRequest.getSupply().getSupplyId())
                .build();
    }
}
