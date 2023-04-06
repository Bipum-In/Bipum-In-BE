package com.sparta.bipuminbe.common.enums;

import lombok.Getter;

@Getter
public enum UseType {
    PERSONAL("개인"),
    COMMON("공용");

    private final String korean;

    UseType(String korean) {
        this.korean = korean;
    }
    }
