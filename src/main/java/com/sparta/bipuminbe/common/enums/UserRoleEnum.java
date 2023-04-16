package com.sparta.bipuminbe.common.enums;

public enum UserRoleEnum {
    USER(Authority.USER, "유저"), // 사용자 권한
    ADMIN(Authority.ADMIN, "비품 총괄 관리자"), // 관리자 권한
    MASTER(Authority.MASTER, "마스터"), // 마스터 권한
    RESPONSIBILITY(Authority.RESPONSIBILITY, "공용 비품 책임자");   // 책임자 권한

    private final String authority;
    private final String korean;

    UserRoleEnum(String authority, String korean) {
        this.authority = authority;
        this.korean = korean;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";  // 유저
        public static final String ADMIN = "ROLE_ADMIN";    // 비품 총괄 관리자
        public static final String MASTER = "ROLE_MASTER";  // 마스터
        public static final String RESPONSIBILITY = "ROLE_RESPONSIBILITY";  // 공용 비품 책임자
    }
}
