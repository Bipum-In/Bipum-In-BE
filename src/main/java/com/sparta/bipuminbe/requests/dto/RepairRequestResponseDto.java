package com.sparta.bipuminbe.requests.dto;

import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RepairRequestResponseDto {
    private String requestType;
    private Long requestId;
    private Boolean isAdmin;
    private String deptName;
    private String empName;
    private String modelName;
    private String image;
    private String content;
    private String requestStatus;
    private LocalDateTime createdAt;

    public static RepairRequestResponseDto of(Requests requests, UserRoleEnum role) {
        User user = requests.getUser();
        Supply supply = requests.getSupply();

        return RepairRequestResponseDto.builder()
                .requestType(requests.getRequestType().getKorean())
                .requestId(requests.getRequestId())
                .isAdmin(role.equals(UserRoleEnum.ADMIN))
                .deptName(user.getDepartment().getDeptName())
                .empName(user.getEmpName())
                .modelName(supply.getModelName())
                .image(requests.getImage())
                .content(requests.getContent())
                .requestStatus(requests.getRequestStatus().getKorean())
                .createdAt(requests.getCreatedAt())
                .build();
    }
}
