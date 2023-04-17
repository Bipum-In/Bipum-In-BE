package com.sparta.bipuminbe.user.dto;

import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.Company;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponseDto {
    private String empName;
    private String deptName;
    private String phone;
    private Boolean alarm;
    private String image;

    public static UserInfoResponseDto of(User user) {
        return UserInfoResponseDto.builder()
                .empName(user.getEmpName())
                .deptName(user.getRole() == UserRoleEnum.MASTER ? Company.BIPUMIN.getCompanyName() : user.getDepartment().getDeptName())
                .phone(user.getPhone())
                .alarm(user.getAlarm())
                .image(user.getImage())
                .build();
    }
}
