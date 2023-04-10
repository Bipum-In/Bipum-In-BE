package com.sparta.bipuminbe.user.dto;

import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class LoginResponseDto{
    private Boolean checkUser;
    private Boolean isAdmin;
    private UserRoleEnum userRole;

    public static LoginResponseDto of(User user, Boolean checkUser) {
        return LoginResponseDto.builder()
                .checkUser(checkUser)
                .isAdmin(user.getRole().equals(UserRoleEnum.ADMIN))
                .userRole(user.getRole())
                .build();
    }
}
