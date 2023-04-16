package com.sparta.bipuminbe.common.enums;

public enum UserRoleEnum {
    USER(Authority.USER), // 사용자 권한
    ADMIN(Authority.ADMIN), // 관리자 권한
    MASTER(Authority.MASTER), // 마스터 권한
    RESPONSIBILITY(Authority.RESPONSIBILITY);   // 책임자 권한

    private final String authority;

    UserRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String MASTER = "ROLE_MASTER";
        public static final String RESPONSIBILITY = "ROLE_RESPONSIBILITY";
    }
}
