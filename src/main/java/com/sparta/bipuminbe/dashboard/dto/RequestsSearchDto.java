package com.sparta.bipuminbe.dashboard.dto;

import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.enums.RequestType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RequestsSearchDto {
    private Long requestId;
    private String requestType;
    private String categoryName;
    private String modelName;
    private String deptName;
    private String empName;
    private String status;
    private String acceptResult;

    public static RequestsSearchDto of(Requests request) {
        Supply supply = request.getSupply();

        return RequestsSearchDto.builder()
                .requestId(request.getRequestId())
                .requestType(request.getRequestType().getKorean())
                .categoryName(request.getRequestType() == RequestType.SUPPLY
                        ? request.getCategory().getCategoryName() : supply.getCategory().getCategoryName())
                .modelName(supply == null ? null : supply.getModelName())
                .deptName(request.getUser().getDepartment().getDeptName())
                .empName(request.getUser().getEmpName())
                .status(request.getRequestStatus().getKorean())
                .acceptResult(request.getAcceptResult() == null ? null : request.getAcceptResult().getKorean())
                .build();
    }
}
