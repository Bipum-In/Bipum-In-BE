package com.sparta.bipuminbe.supply.dto;

import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.RequestType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
public class SupplyHistoryResponseDto {
    private Long requestId;
    private String empName;
    private String deptName;
    private String history;
    private LocalDateTime modifiedAt;

    public static SupplyHistoryResponseDto of(Requests request) {
        User user = request.getUser();
        return SupplyHistoryResponseDto.builder()
                .requestId(request.getRequestId())
                .empName(user == null ? null : user.getEmpName())
                .deptName(user == null ? null : user.getDepartment().getDeptName())
                .history(request.getRequestType() == RequestType.SUPPLY ? "사용" : "반납")
                .modifiedAt(request.getModifiedAt())
                .build();
    }
}
