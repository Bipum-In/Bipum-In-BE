package com.sparta.bipuminbe.requests.dto;

import com.sparta.bipuminbe.common.entity.*;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportRequestResponseDto {
    private Long requestId;
    private Boolean isAdmin;
    private String requestType;
    private String requestStatus;
    private String acceptResult;
    private String deptName;
    private String empName;
    private String categoryName;
    private String modelName;
    private String content;
    private String image;
    private String comment;

    public static ReportRequestResponseDto of(Requests request, UserRoleEnum role) {
        User user = request.getUser();
        Department department = user.getDepartment();
        Supply supply = request.getSupply();
        Category category = supply.getCategory();

        return ReportRequestResponseDto.builder()
                .requestId(request.getRequestId())
                .isAdmin(role.equals(UserRoleEnum.ADMIN))
                .requestType(request.getRequestType().getKorean())
                .requestStatus(request.getRequestStatus().getKorean())
                .acceptResult(request.getAcceptResult().getKorean())
                .deptName(department.getDeptName())
                .empName(user.getEmpName())
                .categoryName(category.getCategoryName())
                .modelName(supply.getModelName())
                .content(request.getContent())
                .image(request.getImage())
                .comment(request.getComment())
                .build();
    }
}
