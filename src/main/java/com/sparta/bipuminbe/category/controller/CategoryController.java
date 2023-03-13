package com.sparta.bipuminbe.category.controller;

import com.sparta.bipuminbe.category.dto.CategoryDto;
import com.sparta.bipuminbe.category.service.CategoryService;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/category")
    @Operation(summary = "카테고리 리스트")
    public ResponseDto<List<CategoryDto>> getCategoryList() {
        return categoryService.getCategoryList();
    }

    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PostMapping("/category")
    @Operation(summary = "카테고리 등록", description = "카테고리 이름(null 불가), 카테고리 이미지(null 가능)")
    public ResponseDto<String> createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.createCategory(categoryDto);
    }

    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PutMapping("/category/{categoryId}")
    @Operation(summary = "카테고리 수정", description = "카테고리 이름(null 불가), 카테고리 이미지(null 가능)")
    public ResponseDto<String> updateCategory(@PathVariable Long categoryId, @RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.updateCategory(categoryId, categoryDto);
    }

    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @DeleteMapping("/category/{categoryId}")
    @Operation(summary = "카테고리 삭제")
    public ResponseDto<String> deleteCategory(@PathVariable Long categoryId) {
        return categoryService.deleteCategory(categoryId);
    }
}