package com.sparta.bipuminbe.department.controller;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import com.sparta.bipuminbe.department.dto.DefaultDeptRequestDto;
import com.sparta.bipuminbe.department.dto.DepartmentDto;
import com.sparta.bipuminbe.department.dto.DeptByEmployeeDto;
import com.sparta.bipuminbe.department.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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


    @Secured(value = {UserRoleEnum.Authority.ADMIN, UserRoleEnum.Authority.MASTER})
    @PostMapping("/dept")
    @Operation(summary = "부서 등록", description = "부서이름만 입력하면 됨, 관리자 권한 필요.")
    public ResponseDto<String> createDept(@RequestBody @Valid DepartmentDto departmentDto) {
        return departmentService.createDept(departmentDto);
    }


    @Secured(value = {UserRoleEnum.Authority.ADMIN, UserRoleEnum.Authority.MASTER})
    @PutMapping("/dept/{deptId}")
    @Operation(summary = "부서 수정", description = "수정할 부서 이름 입력, 관리자 권한 필요.")
    public ResponseDto<String> updateDept(@PathVariable Long deptId, @RequestBody @Valid DepartmentDto departmentDto) {
        return departmentService.updateDept(deptId, departmentDto);
    }


    @Secured(value = {UserRoleEnum.Authority.ADMIN, UserRoleEnum.Authority.MASTER})
    @DeleteMapping("/dept/{deptId}")
    @Operation(summary = "부서 삭제", description = "관리자 권한 필요.")
    public ResponseDto<String> deleteDept(@PathVariable Long deptId,
                                          @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return departmentService.deleteDept(deptId, userDetails.getUser());
    }


    @Secured(value = {UserRoleEnum.Authority.ADMIN, UserRoleEnum.Authority.MASTER})
    @GetMapping("/dept/{deptId}")
    @Operation(summary = "부서별 구성원 조회", description = "관리자/마스터 권한 필요.")
    public ResponseDto<List<DeptByEmployeeDto>> getEmployeeByDept(@PathVariable Long deptId,
                                                                  @RequestParam(defaultValue = "") String keyword) {
        return departmentService.getEmployeeByDept(deptId, keyword);
    }


    @Secured(value = UserRoleEnum.Authority.MASTER)
    @PostMapping("/master/dept")
    @Operation(summary = "부서 초기 세팅(마스터)", description = "부서이름 리스트 보내면 됩니다.")
    public ResponseDto<String> setDefaultDeptList(@RequestBody DefaultDeptRequestDto defaultDeptRequestDto) {
        return departmentService.setDefaultDeptList(defaultDeptRequestDto);
    }

}
