package com.sparta.bipuminbe.common.enums;

import lombok.Getter;

@Getter
public enum SupplyStatusEnum {
    USING("사용중"),
    REPAIRING("수리중"),
    STOCK("재고");

    private final String korean;

    SupplyStatusEnum(String korean) {
        this.korean = korean;
    }
}
