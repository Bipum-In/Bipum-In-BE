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

    public static LoginResponseDto of(User user, Boolean checkUser) {
        return LoginResponseDto.builder()
                .checkUser(checkUser)
                .isAdmin(user.getRole().equals(UserRoleEnum.ADMIN))
                .empName(user.getEmpName())
                .deptName(user.getDepartment().getDeptName())
                .image(user.getImage())
                .build();
    }
}
