package com.sparta.bipuminbe.supply.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ImageResponseDto {
    private String image;

    ImageResponseDto(String image){
        this.image = image;
    }

    public static ImageResponseDto of (String image) {
        return new ImageResponseDto(image);
    }
}
