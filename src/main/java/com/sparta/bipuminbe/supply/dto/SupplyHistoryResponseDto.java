package com.sparta.bipuminbe.supply.dto;

import com.sparta.bipuminbe.common.entity.Partners;
import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.RequestType;
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

        SupplyHistoryResponseDtoBuilder builder = SupplyHistoryResponseDto.builder()
                .requestId(request.getRequestId())
                .empName(user == null ? null : user.getEmpName())
                .deptName(user == null ? null : user.getDepartment().getDeptName())
                .partnersName(partners == null ? null : partners.getPartnersName())
                .modifiedAt(request.getModifiedAt());

        if (request.getRequestType().equals(RequestType.SUPPLY)) {
            builder.history("사용");
        } else if (request.getRequestType().equals(RequestType.RETURN)) {
            builder.history("반납");
        }

        return builder.build();
    }
}
