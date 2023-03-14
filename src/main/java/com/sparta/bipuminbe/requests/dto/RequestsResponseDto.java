package com.sparta.bipuminbe.requests.dto;

import com.sparta.bipuminbe.common.entity.RepairRequest;
import com.sparta.bipuminbe.common.entity.SupplyRequest;
import com.sparta.bipuminbe.common.enums.RequestType;
import com.sparta.bipuminbe.common.exception.CustomException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RequestsResponseDto implements Comparable<RequestsResponseDto> {
    private String type;
    private Long requestId;
    private String title;
    private String userImage;
    private String empName;
    private LocalDateTime createdAt;

    public static RequestsResponseDto of(Object request) {
        String type = "";
        String title = "";
        if (request.getClass().equals(SupplyRequest.class)) {
            type = RequestType.SUPPLY.getKorean();
        } else if (request.getClass().equals(RepairRequest.class)) {
            type = RequestType.REPAIR.getKorean();
        } else if (request.getClass().equals(RepairRequest.class)) {
            type = RequestType.RETURN.getKorean();
        } else{
            throw new CustomException()
        }

        return RequestsResponseDto.builder()
                .type(type)
                .requestId(request.).build();
    }

    @Override
    public int compareTo(RequestsResponseDto o) {
        return this.createdAt.compareTo(o.createdAt);
    }
}
