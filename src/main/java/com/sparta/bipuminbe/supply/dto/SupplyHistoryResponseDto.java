package com.sparta.bipuminbe.supply.dto;

import com.sparta.bipuminbe.common.entity.*;
import com.sparta.bipuminbe.common.enums.RequestType;
import com.sparta.bipuminbe.common.enums.UseType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SupplyHistoryResponseDto {
    private Long requestId;
    private String empName;
    private String deptName;
    private String history;
    private String partnersName;
    private LocalDateTime modifiedAt;

    public static SupplyHistoryResponseDto of(Requests request) {
        User user = request.getUser();
        Partners partners = request.getPartners();
        Department department = request.getDepartment();

        SupplyHistoryResponseDtoBuilder builder = SupplyHistoryResponseDto.builder()
                .requestId(request.getRequestId())
                .partnersName(partners == null ? null : partners.getPartnersName())
                .modifiedAt(request.getModifiedAt());

        if (request.getRequestType() == RequestType.SUPPLY || request.getRequestType() == RequestType.RETURN) {
            // 개인 / 공용 (공용에 대한 request는 department를 저장하고 있다.)
            builder.empName(department == null ? user.getEmpName() : "공용")
                    .deptName(department == null ? user.getDepartment().getDeptName() : department.getDeptName());

            if (request.getRequestType() == RequestType.SUPPLY) {
                builder.history("사용");
            } else {
                builder.history("반납");
            }
        } else {
            // 수리요청/보고서는 신청자로 받고있다.
            builder.empName(user.getEmpName())
                    .deptName(user.getDepartment().getDeptName());
        }

        return builder.build();
    }
}
