package com.sparta.bipuminbe.department.dto;

import com.sparta.bipuminbe.common.entity.Department;
import lombok.Builder;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@Valid
public class DepartmentDto {
    private Long deptId;
    @NotNull
    private String deptName;

    public static DepartmentDto of(Department department) {
        return DepartmentDto.builder()
                .deptId(department.getId())
                .deptName(department.getDeptName())
                .build();
    }
}
