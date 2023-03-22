package com.sparta.bipuminbe.supply.dto;

import lombok.Builder;
import lombok.Getter;
import org.json.JSONObject;

@Getter
@Builder
public class ImageResponseDto {
    private String image;
    private String modelName;

    public static ImageResponseDto of(JSONObject itemJson) {
        return ImageResponseDto.builder()
                .image(itemJson.getString("image"))
                .modelName(itemJson.getString("title"))
                .build();
    }
}
