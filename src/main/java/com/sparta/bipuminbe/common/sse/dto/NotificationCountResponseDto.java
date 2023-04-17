package com.sparta.bipuminbe.common.sse.dto;

import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationCountResponseDto {
    private UserRoleEnum role;
    private Long count;

    public static NotificationCountResponseDto of(UserRoleEnum role, Long count) {
        return NotificationCountResponseDto.builder()
                .role(role)
                .count(count)
                .build();
    }
}
