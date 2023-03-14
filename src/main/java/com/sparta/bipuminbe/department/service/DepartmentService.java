package com.sparta.bipuminbe.department.service;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.Department;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.department.dto.DepartmentDto;
import com.sparta.bipuminbe.department.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public ResponseDto<List<DepartmentDto>> getDeptList() {
        List<Department> departmentList = departmentRepository.findAll();
        List<DepartmentDto> departmentDtoList = new ArrayList<>();
        for (Department department : departmentList) {
            departmentDtoList.add(DepartmentDto.of(department));
        }
        return ResponseDto.success(departmentDtoList);
    }

    @Transactional
    public ResponseDto<String> createDept(DepartmentDto departmentDto) {
        checkDepartment(departmentDto.getDeptName());
        departmentRepository.save(Department.builder().departmentDto(departmentDto).build());
        return ResponseDto.success("부서 등록 완료.");
    }

    @Transactional
    public ResponseDto<String> updateDept(Long deptId, DepartmentDto departmentDto) {
        checkDepartment(departmentDto.getDeptName());
        getDept(deptId).update(departmentDto);
        return ResponseDto.success("부서 수정 완료.");
    }

    @Transactional
    public ResponseDto<String> deleteDept(Long deptId) {
        departmentRepository.delete(getDept(deptId));
        return ResponseDto.success("부서 삭제 완료.");
    }

    private Department getDept(Long deptId) {
        return departmentRepository.findById(deptId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundCategory));
    }

    private void checkDepartment(String deptName) {
        if (departmentRepository.existsByDeptName(deptName)) {
            throw new CustomException(ErrorCode.DuplicatedDepartment);
        }
    }
}
