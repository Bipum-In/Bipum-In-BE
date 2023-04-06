package com.sparta.bipuminbe.user.repository;

import com.sparta.bipuminbe.common.entity.Department;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
