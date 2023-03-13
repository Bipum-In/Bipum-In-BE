package com.sparta.bipuminbe.requests.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.RepairRequest;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RepairRequestDto<T> {

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

    private String image;

    private Boolean isSelf;

    private String status;

    private Long supplyId;

    private User user;

    public static RepairRequestDto of(RepairRequest repairRequest){
        return RepairRequestDto.builder()
                .content(repairRequest.getContent())
                .image(repairRequest.getImage())
                .isSelf(repairRequest.getIsSelf())
                .status(repairRequest.getStatus())
                .supplyId(repairRequest.getSupply().getSupplyId())
                .build();
    }
}
