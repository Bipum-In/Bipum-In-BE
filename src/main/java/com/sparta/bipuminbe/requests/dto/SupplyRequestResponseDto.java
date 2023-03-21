package com.sparta.bipuminbe.requests.dto;

import com.sparta.bipuminbe.common.entity.Category;
import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SupplyRequestResponseDto {
    private String requestType;
    private Long requestId;
    private Boolean isAdmin;
    private String deptName;
    private String empName;
    private Long categoryId;
    private String categoryName;
    private String content;
    private String requestStatus;
    private LocalDateTime createdAt;

    public static SupplyRequestResponseDto of(Requests requests, UserRoleEnum role) {
        User user = requests.getUser();
        Category category = requests.getCategory();

        return SupplyRequestResponseDto.builder()
                .requestType(requests.getRequestType().getKorean())
                .requestId(requests.getRequestId())
                .isAdmin(role.equals(UserRoleEnum.ADMIN))
                .deptName(user.getDepartment().getDeptName())
                .empName(user.getEmpName())
                .categoryId(category.getId())
                .categoryName(category.getCategoryName())
                .content(requests.getContent())
                .requestStatus(requests.getRequestStatus().getKorean())
                .createdAt(requests.getCreatedAt())
                .build();
    }
}
