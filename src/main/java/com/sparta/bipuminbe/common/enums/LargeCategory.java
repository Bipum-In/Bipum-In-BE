package com.sparta.bipuminbe.common.enums;

import lombok.Getter;

@Getter
public enum LargeCategory {
    COMPUTER("컴퓨터"),
    DIGITAL("디지털"),
    ELECTRONICS("가전제품"),
    FURNITURE("가구"),
    ETC("기타");

    private final String korean;

    LargeCategory(String korean) {
        this.korean = korean;
    }
}
