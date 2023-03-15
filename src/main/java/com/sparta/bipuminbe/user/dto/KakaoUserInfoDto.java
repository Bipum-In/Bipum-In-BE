package com.sparta.bipuminbe.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfoDto {
    private Long id;
    private String username;
    private String image;

    @Builder
    public KakaoUserInfoDto(Long id, String username, String image) {
        this.id = id;
        this.username = username;
        this.image = image;
    }
}