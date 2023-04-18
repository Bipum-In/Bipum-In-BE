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

    List<User> findByDepartmentAndDeletedFalse(Department department);

    Optional<User> findByGoogleIdAndDeletedFalse(String googleId);

    boolean existsByDepartment_IdAndDeletedFalse(Long id);

    Optional<User> findByEmpNameAndDepartment_DeptNameAndDeletedFalse(String empName, String deptName);

//    @Query(value = "SELECT u FROM users u " +
//            "inner join Department d on d = u.department " +
//            "WHERE d.id = :deptId and u.deleted = false " +
//            "and (u.username like :keyword or lower(u.empName) like lower(:keyword) " +
//            "or u.phone like :keyword)")
//    List<User> findByDeptByEmployee(@Param("deptId") Long deptId, @Param("keyword") String keyword);


    Optional<User> findByUsernameAndPassword(String username, String password);
}
