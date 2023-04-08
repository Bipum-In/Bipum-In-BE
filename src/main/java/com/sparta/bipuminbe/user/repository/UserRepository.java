package com.sparta.bipuminbe.user.repository;

import com.sparta.bipuminbe.common.entity.Department;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findById(Long id);

    void deleteByGoogleId(String GoogleId);

    List<User> findByRoleAndAlarmAndDeletedFalse(UserRoleEnum admin, boolean b);

    List<User> findByDeletedFalse();

    Optional<User> findByIdAndDeletedFalse(Long userId);

    Optional<User> findByUsernameAndDeletedFalse(String username);

    List<User> findByDepartmentAndDeletedFalse(Department department);

    Optional<User> findByGoogleIdAndDeletedFalse(String googleId);

    boolean existsByDepartment_IdAndDeletedFalse(Long id);

    Optional<User> findByEmpNameAndDepartment_DeptNameAndDeletedFalse(String empName, String deptName);

    @Query(value = "SELECT * FROM users WHERE department_id = :department_id and deleted = false", nativeQuery = true)
    List<User> findByDeptByEmployee(@Param("department_id") Long deptId);
}
