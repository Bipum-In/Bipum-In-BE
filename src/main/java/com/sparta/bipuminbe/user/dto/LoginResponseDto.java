package com.sparta.bipuminbe.user.dto;

import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {
    private Boolean checkUser;
    private Boolean isAdmin;
    private String empName;
    private String deptName;
    private String image;
    private Long userId;

    public static LoginResponseDto of(User user, Boolean checkUser) {
        return LoginResponseDto.builder()
                .checkUser(checkUser)
                .isAdmin(user.getRole().equals(UserRoleEnum.ADMIN))
                .empName(user.getEmpName())
                .deptName(user.getDepartment() == null ? null : user.getDepartment().getDeptName())
                .image(user.getImage())
                .userId(user.getId())
                .build();
    }
}
