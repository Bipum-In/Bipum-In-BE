package com.sparta.bipuminbe.category.dto;

import com.sparta.bipuminbe.common.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Valid
@Builder
public class CategoryDto {
    private Long categoryId;
    @NotNull
    private String largeCategory;
    @NotBlank
    @Pattern(regexp = "^[0-9a-zA-Z가-힣 ]*${4,30}")
    private String categoryName;

    public static CategoryDto of(Category category) {
        return CategoryDto.builder()
                .categoryId(category.getId())
                .largeCategory(category.getLargeCategory().getKorean())
                .categoryName(category.getCategoryName())
                .build();
    }
}
