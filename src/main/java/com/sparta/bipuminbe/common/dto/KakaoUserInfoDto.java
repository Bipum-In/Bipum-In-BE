package com.sparta.bipuminbe.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfoDto {
    private Long id;
    private String username;
    private String image;

    public KakaoUserInfoDto(Long id, String username, String image) {
        this.id = id;
        this.username = username;
        this.image = image;
    }
}