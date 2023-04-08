package com.sparta.bipuminbe.department.dto;

import com.sparta.bipuminbe.common.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeptByEmployeeDto {
    private String image;
    private String empName;
    private String phone;
    private String username;

     //공용 비품 책임자 권한? 추후 권한 추가 시 수정
//    private String role;

    public static DeptByEmployeeDto of(User user) {
        return builder()
                .image(user.getImage())
                .empName(user.getEmpName())
                .phone(user.getPhone())
                .username(user.getUsername())
                .build();
    }
}
