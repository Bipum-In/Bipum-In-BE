package com.sparta.bipuminbe.common.enums;

import lombok.Getter;

@Getter
public enum RequestType {
    SUPPLY("비품 요청"),
    REPAIR("수리 요청"),
    RETURN("반납 요청"),
    REPORT("보고서 결재");

    private final String korean;

    RequestType(String korean){
        this.korean = korean;
    }
}
