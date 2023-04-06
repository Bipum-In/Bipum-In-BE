package com.sparta.bipuminbe.common.enums;

import lombok.Getter;

@Getter
public enum AcceptResult {
    ACCEPT("승인"),
    DECLINE("거절"),
    DISPOSE("폐기"),
    ASSIGN("사용자 배정");

    private final String korean;

    AcceptResult(String korean) {
        this.korean = korean;
    }
}
