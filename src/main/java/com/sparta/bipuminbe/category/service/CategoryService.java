package com.sparta.bipuminbe.category.service;

import com.sparta.bipuminbe.category.dto.CategoryDto;
import com.sparta.bipuminbe.category.repository.CategoryRepository;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.Category;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public ResponseDto<List<CategoryDto>> getCategoryList() {
        List<Category> categoryList = categoryRepository.findAll();
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        for (Category category : categoryList) {
            categoryDtoList.add(CategoryDto.of(category));
        }
        return ResponseDto.success(categoryDtoList);
    }

    @Transactional
    public ResponseDto<String> createCategory(CategoryDto categoryDto) {
        checkCategory(categoryDto.getCategoryName());
        categoryRepository.save(Category.builder().categoryDto(categoryDto).build());
        return ResponseDto.success("카테고리 등록 완료.");
    }

    @Transactional
    public ResponseDto<String> updateCategory(Long categoryId, CategoryDto categoryDto) {
        checkCategory(categoryDto.getCategoryName());
        getCategory(categoryId).update(categoryDto);
        return ResponseDto.success("카테고리 수정 완료.");
    }

    @Transactional
    public ResponseDto<String> deleteCategory(Long categoryId) {
        categoryRepository.delete(getCategory(categoryId));
        return ResponseDto.success("카테고리 삭제 완료.");
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundCategory));
    }

    private void checkCategory(String categoryName) {
        if(categoryRepository.existsByCategoryName(categoryName)){
            throw new CustomException(ErrorCode.DuplicatedCategory);
        }
    }
}
