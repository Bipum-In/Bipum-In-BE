package com.sparta.bipuminbe.department.dto;

import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeptByEmployeeDto {
    private String image;
    private String empName;
    private String phone;
    private String username;
    private String authority;

    public static DeptByEmployeeDto of(User user) {
        return builder()
                .image(user.getImage())
                .empName(user.getEmpName())
                .phone(user.getPhone())
                .username(user.getUsername())
                .authority(user.getRole() != UserRoleEnum.USER ? user.getRole().getKorean() : null)
                .build();
    }
}
