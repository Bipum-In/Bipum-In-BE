package com.sparta.bipuminbe.department.service;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.Department;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.department.dto.DepartmentDto;
import com.sparta.bipuminbe.department.repository.DepartmentRepository;
import com.sparta.bipuminbe.supply.repository.SupplyRepository;
import com.sparta.bipuminbe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final SupplyRepository supplyRepository;

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
        if (checkDepartment(departmentDto.getDeptName())) {
            throw new CustomException(ErrorCode.DuplicatedDepartment);
        }
        departmentRepository.save(Department.builder().departmentDto(departmentDto).build());
        return ResponseDto.success("부서 등록 완료.");
    }

    private Department getDept(Long deptId) {
        return departmentRepository.findById(deptId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundCategory));
    }

    private Boolean checkDepartment(String deptName) {
        return departmentRepository.existsByDeptName(deptName);
    }

    @Transactional
    public ResponseDto<String> updateDept(Long deptId, DepartmentDto departmentDto) {
        Department department = getDept(deptId);
        // 바꾸려는 부서명이 이미 존재하는 부서명인지 체크. (자기를 제외하고 체크 해야함.)
        if (!department.getDeptName().equals(departmentDto.getDeptName()) && checkDepartment(departmentDto.getDeptName())) {
            throw new CustomException(ErrorCode.DuplicatedDepartment);
        }
        department.update(departmentDto);
        return ResponseDto.success("부서 수정 완료.");
    }

    @Transactional
    public ResponseDto<String> deleteDept(Long deptId) {
        // 삭제 전 사원이 없는지 체크.
        if (userRepository.existsByDepartment_IdAndDeletedFalse(deptId)) {
            throw new CustomException(ErrorCode.ExistsUserInDepartment);
        }
        // 공용 비품 호출 및 반납 처리.
        List<Supply> commonSupplyList = supplyRepository.findByDepartment_Id(deptId);
        for (Supply supply : commonSupplyList) {
            supply.returnSupply();
        }
        departmentRepository.delete(getDept(deptId));
        return ResponseDto.success("부서 삭제 완료.");
    }
}
