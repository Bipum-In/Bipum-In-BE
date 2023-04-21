package com.sparta.bipuminbe.common.enums;

import lombok.Getter;

@Getter
public enum Company {
    BIPUMIN("성인컴퍼니");

    private final String companyName;

    Company(String companyName) {
        this.companyName = companyName;
    }
}
