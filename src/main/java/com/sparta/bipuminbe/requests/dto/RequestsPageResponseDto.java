package com.sparta.bipuminbe.requests.dto;

import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.RequestType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RequestsPageResponseDto {
    private Long requestId;
    private String requestType;
    private String empName;
    private String deptName;
    private String categoryName;
    private String modelName;
    private LocalDateTime createdAt;
    private String status;
    private String acceptResult;

    public static RequestsPageResponseDto of(Requests requests) {
        User user = requests.getUser();

        RequestsPageResponseDtoBuilder builder = RequestsPageResponseDto.builder()
                .requestId(requests.getRequestId())
                .requestType(requests.getRequestType().getKorean())
                .empName(user.getEmpName())
                .deptName(user.getDepartment().getDeptName())
                .createdAt(requests.getCreatedAt())
                .status(requests.getRequestStatus().getKorean());

        if (requests.getRequestType().equals(RequestType.SUPPLY)) {
            builder.categoryName(requests.getCategory().getCategoryName());
        } else {
            Supply supply = requests.getSupply();
            builder.categoryName(supply.getCategory().getCategoryName())
                    .modelName(supply.getModelName());
        }

        if (requests.getAcceptResult() != null) {
            builder.acceptResult(requests.getAcceptResult().getKorean());
        }

        return builder.build();
    }
}