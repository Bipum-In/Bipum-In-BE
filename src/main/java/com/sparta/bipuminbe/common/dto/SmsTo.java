package com.sparta.bipuminbe.common.dto;

import lombok.Builder;

@Builder
public class SmsTo {
    private String to;

    public static SmsTo of(String toPhone) {
        return SmsTo.builder()
                .to(toPhone)
                .build();
    }
}
