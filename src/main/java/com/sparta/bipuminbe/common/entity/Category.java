package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.category.dto.CategoryDto;
import com.sparta.bipuminbe.common.enums.LargeCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String categoryName;

    @Enumerated(EnumType.STRING)
    private LargeCategory largeCategory;

    private String categoryImage;

    @Builder
    public Category(CategoryDto categoryDto) {
        this.categoryName = categoryDto.getCategoryName();
        this.largeCategory = LargeCategory.valueOf(categoryDto.getLargeCategory());
        this.categoryImage = categoryDto.getCategoryImage();
    }

    public void update(CategoryDto categoryDto) {
        this.categoryName = categoryDto.getCategoryName();
        this.largeCategory = LargeCategory.valueOf(categoryDto.getLargeCategory());
        this.categoryImage = categoryDto.getCategoryImage();
    }
}
