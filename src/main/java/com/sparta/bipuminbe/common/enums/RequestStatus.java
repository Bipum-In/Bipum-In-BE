package com.sparta.bipuminbe.common.enums;

import lombok.Getter;

@Getter
public enum RequestStatus {
    UNPROCESSED("처리전"),
    REPAIRING("수리중"),
    PROCESSED("처리완료");

    private final String korean;

    RequestStatus(String korean) {
        this.korean = korean;
    }
}
