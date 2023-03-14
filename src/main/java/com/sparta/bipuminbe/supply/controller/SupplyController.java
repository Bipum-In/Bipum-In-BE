package com.sparta.bipuminbe.supply.controller;

import com.sparta.bipuminbe.category.dto.CategoryDto;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.supply.service.SupplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SupplyController {
    private final SupplyService supplyService;

    @GetMapping("/supply")
    @Operation(summary = "비품 리스트")
    public List<SupplyResponseDto> getSupplyList() {
        return supplyService.getSupplyList();
    }

    @GetMapping("/supply/{studyId}")
    public SupplyWholeResponseDto getSupply(
            @PathVariable Long supplyId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return supplyService.getSupply(supplyId, userDetails);
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
