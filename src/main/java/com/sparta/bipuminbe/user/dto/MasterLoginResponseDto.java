package com.sparta.bipuminbe.user.dto;

import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MasterLoginResponseDto {
    private Boolean checkDept;
    private String role;

    public static MasterLoginResponseDto of(Boolean checkDept) {
        return MasterLoginResponseDto.builder()
                .checkDept(checkDept)
                .role(UserRoleEnum.MASTER.getKorean())
                .build();
    }
}
