package com.sparta.bipuminbe.common.queryDSL.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.bipuminbe.common.entity.QDepartment;
import com.sparta.bipuminbe.common.entity.QUser;
import com.sparta.bipuminbe.common.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public List<User> findByDeptByEmployee(Long deptId, String keyword) {
        QUser user = QUser.user;
        QDepartment department = QDepartment.department;

        return jpaQueryFactory.select(user)
                .from(user).
                innerJoin(user.department, department)
                .where(department.id.eq(deptId)
                        .and(user.deleted.eq(false))
                        .and(user.username.contains(keyword)
                                .or(user.empName.lower().contains(keyword.toLowerCase()))
                                .or(user.phone.contains(keyword))))
                .fetch();
    }
}
