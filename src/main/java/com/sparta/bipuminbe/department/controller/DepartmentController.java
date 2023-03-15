package com.sparta.bipuminbe.department.controller;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.department.dto.DepartmentDto;
import com.sparta.bipuminbe.department.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping("/dept")
    @Operation(summary = "부서 목록", description = "SelectBox용")
    public ResponseDto<List<DepartmentDto>> getDeptList() {
        return departmentService.getDeptList();
    }

//    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PostMapping("/dept")
    @Operation(summary = "부서 등록", description = "부서이름만 입력하면 됨.")
    public ResponseDto<String> createDept(@RequestBody @Valid DepartmentDto departmentDto) {
        return departmentService.createDept(departmentDto);
    }

//    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PutMapping("/dept/{deptId}")
    @Operation(summary = "부서 수정", description = "수정할 부서 이름 입력.")
    public ResponseDto<String> updateDept(@PathVariable Long deptId, @RequestBody @Valid DepartmentDto departmentDto) {
        return departmentService.updateDept(deptId, departmentDto);
    }

//    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @DeleteMapping("/dept/{deptId}")
    @Operation(summary = "부서 삭제")
    public ResponseDto<String> deleteDept(@PathVariable Long deptId) {
        return departmentService.deleteDept(deptId);
    }
}
