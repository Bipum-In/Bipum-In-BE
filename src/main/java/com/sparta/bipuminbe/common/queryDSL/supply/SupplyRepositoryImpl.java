package com.sparta.bipuminbe.common.queryDSL.supply;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.bipuminbe.common.entity.*;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.SupplyStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class SupplyRepositoryImpl implements SupplyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public Long countTotal(Long categoryId) {
        QSupply supply = QSupply.supply;

        return (long) queryFactory.selectFrom(supply)
                .where(supply.category.id.eq(categoryId).and(supply.deleted.eq(false)))
                .fetch().size();
    }

    public Long countBySupplyStatus(Long categoryId, SupplyStatusEnum supplyStatusEnum) {
        QSupply supply = QSupply.supply;

        return (long) queryFactory.selectFrom(supply)
                .where(supply.category.id.eq(categoryId)
                        .and(supply.status.eq(supplyStatusEnum))
                        .and(supply.deleted.eq(false))).fetch().size();
    }


    @Override
    public Page<Supply> getSupplyList(String keyword, Set<Long> categoryIdQuery,
                                      Set<SupplyStatusEnum> statusQuery, Pageable pageable) {
        QSupply supply = QSupply.supply;
        QCategory category = QCategory.category;
        QUser user = QUser.user;
        QPartners partners = QPartners.partners;
        QDepartment department_user = QDepartment.department;
        QDepartment department_supply = QDepartment.department;

        JPAQuery<Supply> query = queryFactory.selectFrom(supply)
                .distinct()
                .innerJoin(supply.category, category)
                .leftJoin(supply.partners, partners)
                .leftJoin(supply.user, user)
                .leftJoin(user.department, department_user)
                .leftJoin(supply.department, department_supply)
                .where((user.empName.containsIgnoreCase(keyword)
                        .or(category.categoryName.containsIgnoreCase(keyword))
                        .or(supply.modelName.containsIgnoreCase(keyword))
                        .or(supply.serialNum.containsIgnoreCase(keyword))
                        .or(partners.partnersName.containsIgnoreCase(keyword))
                        .or(department_supply.deptName.containsIgnoreCase(keyword))
                        .or(department_user.deptName.containsIgnoreCase(keyword))
                ).and(category.id.in(categoryIdQuery))
                        .and(supply.status.in(statusQuery))
                        .and(supply.deleted.eq(false)))
                .orderBy(supply.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<Supply> supplyList = query.fetch();
        long totalCount = query.fetchCount();
        return new PageImpl<>(supplyList, pageable, totalCount);
    }


    @Override
    public List<Supply> getMySupply(User user, Long categoryId, Set<RequestStatus> statusQuery) {
        QSupply supply = QSupply.supply;
        QCategory category = QCategory.category;
        QRequests requests = QRequests.requests;
        QUser users = QUser.user;

        // 요청이 이미 들어간 비품.
        Set<Long> requestsIdSet = getUnprocessSupplySet(statusQuery, supply, requests);

        return queryFactory.selectFrom(supply)
                .distinct()
                .innerJoin(supply.category, category)
                .innerJoin(supply.user, users)
                .where(users.eq(user), category.id.eq(categoryId),
                        supply.deleted.eq(false), supply.supplyId.notIn(requestsIdSet))
                .fetch();
    }


    // select subQuery는 돌때마다 새로 한다고 한다. 그냥 미리 해주자.
    private Set<Long> getUnprocessSupplySet(Set<RequestStatus> statusQuery, QSupply supply, QRequests requests) {
        Set<Long> requestsIdSet = new HashSet<>(queryFactory.selectDistinct(supply.supplyId)
                .from(requests)
                .innerJoin(requests.supply, supply)
                .where(requests.requestStatus.in(statusQuery), supply.isNotNull())
                .fetch());
        return requestsIdSet;
    }


    @Override
    public List<Supply> getMyCommonSupply(Department department, Long categoryId, Set<RequestStatus> statusQuery) {
        QSupply supply = QSupply.supply;
        QCategory category = QCategory.category;
        QRequests requests = QRequests.requests;
        QDepartment qDepartment = QDepartment.department;

        // 요청이 이미 들어간 비품.
        Set<Long> requestsIdSet = getUnprocessSupplySet(statusQuery, supply, requests);

        return queryFactory.selectFrom(supply)
                .distinct()
                .innerJoin(supply.category, category)
                .innerJoin(supply.department, qDepartment)
                .where(qDepartment.eq(department), category.id.eq(categoryId),
                        supply.deleted.eq(false), supply.supplyId.notIn(requestsIdSet))
                .fetch();
    }

}
