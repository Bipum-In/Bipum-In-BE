package com.sparta.bipuminbe.department.repository;

import com.sparta.bipuminbe.common.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department,Long> {
}
