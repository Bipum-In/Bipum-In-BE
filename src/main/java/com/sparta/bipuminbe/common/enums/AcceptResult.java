package com.sparta.bipuminbe.common.enums;

import lombok.Getter;

@Getter
public enum AcceptResult {
    ACCEPT("승인"),
    DECLINE("거절"),
    DISPOSE("폐기");

    private final String korean;

    AcceptResult(String korean) {
        this.korean = korean;
    }
}
