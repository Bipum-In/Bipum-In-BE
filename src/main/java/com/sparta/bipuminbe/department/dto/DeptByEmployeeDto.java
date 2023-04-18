package com.sparta.bipuminbe.department.dto;

import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeptByEmployeeDto {
    private Long userId;
    private String image;
    private String empName;
    private String phone;
    private String username;
    private String authority;

    public static DeptByEmployeeDto of(User user) {
        return builder()
                .userId(user.getId())
                .image(user.getImage())
                .empName(user.getEmpName())
                .phone(user.getPhone())
                .username(user.getUsername())
                // 유저가 권한을 가지고 있다면 표기한다.
                .authority(user.getRole() != UserRoleEnum.USER ? user.getRole().getKorean() : null)
                .build();
    }
}
