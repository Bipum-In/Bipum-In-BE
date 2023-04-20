package com.sparta.bipuminbe.category.controller;

import com.sparta.bipuminbe.category.dto.CategoryDto;
import com.sparta.bipuminbe.category.service.CategoryService;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.enums.LargeCategory;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/category")
    @Operation(summary = "카테고리 리스트", description = "SelectBox용. 전체조회(ALL) or 대분류이름(영어). <br>" +
            "ALL/COMPUTER/DIGITAL/ELECTRONICS/FURNITURE/ETC")
    public ResponseDto<List<CategoryDto>> getCategoryList(@RequestParam(defaultValue = "ALL") String largeCategory) {
        return categoryService.getCategoryList(largeCategory);
    }

    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PostMapping("/category")
    @Operation(summary = "카테고리 등록", description = "카테고리 이름(null 불가), 카테고리 대분류(null 불가), 카테고리 이미지(null 가능), 관리자 권한 필요.")
    public ResponseDto<String> createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.createCategory(categoryDto);
    }

    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PutMapping("/category/{categoryId}")
    @Operation(summary = "카테고리 수정", description = "카테고리 이름(null 불가), 카테고리 이미지(null 가능), 관리자 권한 필요.")
    public ResponseDto<String> updateCategory(@PathVariable Long categoryId, @RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.updateCategory(categoryId, categoryDto);
    }

    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @DeleteMapping("/category/{categoryId}")
    @Operation(summary = "카테고리 삭제", description = "관리자 권한 필요.")
    public ResponseDto<String> deleteCategory(@PathVariable Long categoryId) {
        return categoryService.deleteCategory(categoryId);
    }

    @GetMapping("/category/myLargeCategory")
    @Operation(summary = "내가 가진 LargeCategory 목록 (유저요청 페이지) *신규 Api*", description = "가지고 있는 LargeCategory만 가져온다.")
    public ResponseDto<List<LargeCategory>> getMyLargeCategory(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return categoryService.getMyLargeCategory(userDetails.getUser());
    }

    @GetMapping("/category/myCategory")
    @Operation(summary = "내가 가진 Category 목록 (유저요청 페이지) *신규 Api*", description = "가지고 있는 Category만 가져온다.")
    public ResponseDto<List<CategoryDto>> getMyCategory(@RequestParam LargeCategory largeCategory,
                                                        @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return categoryService.getMyCategory(largeCategory, userDetails.getUser());
    }

    @GetMapping("/category/common/myLargeCategory")
    @Operation(summary = "공용 골랐을 때 LargeCategory 목록 (유저요청 페이지) *신규 Api*", description = "가지고 있는 공용 LargeCategory")
    public ResponseDto<List<LargeCategory>> getMyCommonLargeCategory(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return categoryService.getMyCommonLargeCategory(userDetails.getUser());
    }

    @GetMapping("/category/common/myCategory")
    @Operation(summary = "공용 골랐을 때 Category 목록 (유저요청 페이지) *신규 Api*", description = "가지고 있는 공용 Category만 가져온다.")
    public ResponseDto<List<CategoryDto>> getMyCommonCategory(@RequestParam LargeCategory largeCategory,
                                                              @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return categoryService.getMyCommonCategory(largeCategory, userDetails.getUser());
    }
}