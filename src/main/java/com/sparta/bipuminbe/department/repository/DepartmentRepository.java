package com.sparta.bipuminbe.department.repository;

import com.sparta.bipuminbe.common.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department,Long> {
    // 부서명 중복 체크.
    boolean existsByDeptNameAndDeletedFalse(String deptName);

    // 부서 리스트 전체 조회.
    List<Department> findByDeletedFalse();

    // 등록, 수정시 삭제된 부서 체크.
    Optional<Department> findByDeptNameAndDeletedTrue(String deptName);

    // 부서 조회.
    Optional<Department> findByIdAndDeletedFalse(Long deptId);
}
