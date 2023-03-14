package com.sparta.bipuminbe.category.dto;

import com.sparta.bipuminbe.common.entity.Category;
import lombok.Builder;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@Valid
public class CategoryDto {
    private Long categoryId;
    @NotNull
    private String categoryName;
    private String categoryImage;

    public static CategoryDto of(Category category) {
        return CategoryDto.builder()
                .categoryId(category.getId())
                .categoryName(category.getCategoryName())
                .categoryImage(category.getCategoryImage())
                .build();
    }
}
