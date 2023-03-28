package com.sparta.bipuminbe.user.repository;

import com.sparta.bipuminbe.common.entity.Department;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByKakaoId(Long kakaoId);
    List<User> findByDepartment(Department department);

    Optional<User> findById(Long id);

    void deleteByKakaoId(Long kakaoId);

    List<User> findByRoleAndAlarm(UserRoleEnum role, Boolean alarm);

    List<User> findByDepartment_Id(Long id);
}
