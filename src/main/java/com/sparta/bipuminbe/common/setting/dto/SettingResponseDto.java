package com.sparta.bipuminbe.common.setting.dto;

import com.sparta.bipuminbe.category.dto.CategoryDto;
import com.sparta.bipuminbe.department.dto.DepartmentDto;
import com.sparta.bipuminbe.partners.dto.PartnersDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SettingResponseDto {
    private List<PartnersDto> partnersDtos;
    private List<DepartmentDto> departmentDtos;
    private List<CategoryDto> categoryDtos;

    public static SettingResponseDto of(List<PartnersDto> partnersDtos,
                                        List<DepartmentDto> departmentDtos,
                                        List<CategoryDto> categoryDtos) {
        return builder()
                .partnersDtos(partnersDtos)
                .departmentDtos(departmentDtos)
                .categoryDtos(categoryDtos)
                .build();
    }
}
