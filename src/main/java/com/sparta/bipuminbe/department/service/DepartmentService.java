package com.sparta.bipuminbe.department.service;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.Department;
import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.*;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.department.dto.DefaultDeptRequestDto;
import com.sparta.bipuminbe.department.dto.DepartmentDto;
import com.sparta.bipuminbe.department.dto.DeptByEmployeeDto;
import com.sparta.bipuminbe.department.repository.DepartmentRepository;
import com.sparta.bipuminbe.requests.repository.RequestsRepository;
import com.sparta.bipuminbe.supply.repository.SupplyRepository;
import com.sparta.bipuminbe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final SupplyRepository supplyRepository;
    private final RequestsRepository requestsRepository;


    // 부서 목록 조회.(SelectBox.)
    @Transactional(readOnly = true)
    public ResponseDto<List<DepartmentDto>> getDeptList() {
        List<Department> departmentList = departmentRepository.findByDeletedFalse();
        List<DepartmentDto> departmentDtoList = new ArrayList<>();
        for (Department department : departmentList) {
            departmentDtoList.add(DepartmentDto.of(department));
        }
        return ResponseDto.success(departmentDtoList);
    }


    // 부서 등록.
    @Transactional
    public ResponseDto<String> createDept(DepartmentDto departmentDto) {
        if (checkDepartment(departmentDto.getDeptName())) {
            throw new CustomException(ErrorCode.DuplicatedDepartment);
        }
        checkDeletedDepartment(departmentDto.getDeptName());
        departmentRepository.save(Department.builder().deptName(departmentDto.getDeptName()).build());
        return ResponseDto.success("부서 등록 완료.");
    }

    // 삭제된 부서 체크. (부서명 Unique)
    private void checkDeletedDepartment(String deptName) {
        // 삭제된 부서명 변경 로직.
        // 삭제할 때 처리안했던 이유는, 최대한 그대로의 이름을 history에 보존하고 싶어서.
        Optional<Department> optionalDepartment = departmentRepository.findByDeptNameAndDeletedTrue(deptName);
        if (optionalDepartment.isPresent()) {
            Department department = optionalDepartment.get();
            department.update(department.getDeptName() + "(삭제됨#" + department.getId() + ")");
        }
    }

    private Department getDept(Long deptId) {
        return departmentRepository.findByIdAndDeletedFalse(deptId).orElseThrow(
                () -> new CustomException(ErrorCode.NotFoundCategory));
    }

    private Boolean checkDepartment(String deptName) {
        return departmentRepository.existsByDeptNameAndDeletedFalse(deptName);
    }


    // 부서 수정.
    @Transactional
    public ResponseDto<String> updateDept(Long deptId, DepartmentDto departmentDto) {
        Department department = getDept(deptId);
        // 바꾸려는 부서명이 이미 존재하는 부서명인지 체크. (자기를 제외하고 체크 해야함.)
        if (!department.getDeptName().equals(departmentDto.getDeptName()) && checkDepartment(departmentDto.getDeptName())) {
            throw new CustomException(ErrorCode.DuplicatedDepartment);
        }
        checkDeletedDepartment(departmentDto.getDeptName());
        department.update(departmentDto.getDeptName());
        return ResponseDto.success("부서 수정 완료.");
    }


    // 부서 삭제.
    @Transactional
    public ResponseDto<String> deleteDept(Long deptId, User admin) {
        // 삭제 전 사원이 없는지 체크.
        if (userRepository.existsByDepartment_IdAndDeletedFalse(deptId)) {
            throw new CustomException(ErrorCode.ExistsUserInDepartment);
        }
        // 공용 비품 호출 및 반납 처리.
        List<Supply> commonSupplyList = supplyRepository.findByDepartment_Id(deptId);
        for (Supply supply : commonSupplyList) {
            // history 생성을 위한 기록.
            String content = "부서 삭제에 의한 자동 반납 처리.";
            requestsRepository.save(Requests.builder()
                    .content(content)
                    .requestType(RequestType.RETURN)
                    .requestStatus(RequestStatus.PROCESSED)
                    .acceptResult(AcceptResult.ACCEPT)
                    .supply(supply)
                    .useType(supply.getUseType())
                    .user(admin)
                    .department(supply.getDepartment())
                    .admin(admin)
                    .build());

            // 비품 상태 처리.
            supply.returnSupply();
        }
        departmentRepository.delete(getDept(deptId));
        return ResponseDto.success("부서 삭제 완료.");
    }


    // 부서별 사원 조회. 검색 가능.
    @Transactional(readOnly = true)
    public ResponseDto<List<DeptByEmployeeDto>> getEmployeeByDept(Long deptId, String keyword) {
        List<User> employees = userRepository.findByDeptByEmployee(deptId, keyword);
        List<DeptByEmployeeDto> deptByEmployeeDtos = new ArrayList<>();

        for (User employee : employees) {
            deptByEmployeeDtos.add(DeptByEmployeeDto.of(employee));
        }
        return ResponseDto.success(deptByEmployeeDtos);
    }


    // 부서 초기 등록.(MASTER 최초 로그인)
    @Transactional
    public ResponseDto<String> setDefaultDeptList(DefaultDeptRequestDto defaultDeptRequestDto) {
        // 초기세팅은 부서가 없을 때 띄울거다.
        List<Department> departmentList = new ArrayList<>();
        if (defaultDeptRequestDto.getDeptList() == null || defaultDeptRequestDto.getDeptList().size() == 0) {
            throw new CustomException(ErrorCode.AtLeastOneNeedDept);
        }
        for (String deptName : defaultDeptRequestDto.getDeptList()) {
            checkDeletedDepartment(deptName);
            departmentList.add(Department.builder().deptName(deptName).build());
        }
        departmentRepository.saveAll(departmentList);
        return ResponseDto.success("부서 초기 세팅 완료.");
    }
}
