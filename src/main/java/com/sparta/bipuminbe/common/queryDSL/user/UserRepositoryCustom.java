package com.sparta.bipuminbe.common.queryDSL.user;

import com.sparta.bipuminbe.common.entity.User;

import java.util.List;

public interface UserRepositoryCustom {
    List<User> findByDeptByEmployee(Long deptId, String keyword);
}
