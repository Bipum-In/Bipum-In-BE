package com.sparta.bipuminbe.supply.dto;

import com.sparta.bipuminbe.category.dto.CategoryDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class SupplyCategoryDto {

    private List<CategoryDto> categoryDtoList = new ArrayList<>();
    private List<SupplyResponseDto> supplyResponseDtoList = new ArrayList<>();

    SupplyCategoryDto(List<CategoryDto> categoryDtoList, List<SupplyResponseDto> supplyResponseDtoList){
        this.categoryDtoList = categoryDtoList;
        this.supplyResponseDtoList = supplyResponseDtoList;
    }

    public static SupplyCategoryDto of(List<CategoryDto> categoryDtoList, List<SupplyResponseDto> supplyResponseDtoList){
        return new SupplyCategoryDto(categoryDtoList, supplyResponseDtoList);
    }
}
