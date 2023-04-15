package com.sparta.bipuminbe.department.dto;

import com.sparta.bipuminbe.common.entity.Department;
import lombok.Builder;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Builder
@Valid
public class DepartmentDto {
    private Long deptId;
    @NotBlank
    @Pattern(regexp = "^[0-9a-zA-Z가-힣 ]*${4,30}")
    private String deptName;

    public static DepartmentDto of(Department department) {
        return DepartmentDto.builder()
                .deptId(department.getId())
                .deptName(department.getDeptName())
                .build();
    }
}
