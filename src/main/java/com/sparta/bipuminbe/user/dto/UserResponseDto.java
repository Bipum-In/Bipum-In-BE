package com.sparta.bipuminbe.user.dto;

import com.sparta.bipuminbe.common.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {
    private Long userId;
    private String empName;

    public static UserResponseDto of(User user) {
        return UserResponseDto.builder()
                .userId(user.getId())
                .empName(user.getEmpName())
                .build();
    }
}
