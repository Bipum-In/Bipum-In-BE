package com.sparta.bipuminbe.common.setting.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SwitchResponseDto {
    String message;
    Boolean alarm;

    public static SwitchResponseDto of(String message, Boolean alarm){
        return builder().message(message).alarm(alarm).build();
    }
}
