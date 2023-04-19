package com.sparta.bipuminbe.common.queryDSL.requests;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.bipuminbe.common.entity.*;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.RequestType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RequestsRepositoryImpl implements RequestsRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public Long countRequestByType(RequestType requestType, RequestStatus requestStatus) {
        QRequests requests = QRequests.requests;
        return (long) queryFactory.selectFrom(requests)
                .where(requests.requestType.eq(requestType).and(requests.requestStatus.ne(requestStatus)))
                .fetch().size();
    }

    public LocalDateTime requestsModifiedAt(RequestType requestType) {
        QRequests requests = QRequests.requests;
        return queryFactory.select(requests.modifiedAt.max())
                .from(requests).where(requests.requestType.eq(requestType)).fetchOne();
    }

    public Long countMyRequestByType(RequestType requestType, RequestStatus requestStatus, Long userId) {
        QRequests requests = QRequests.requests;
        return (long) queryFactory.selectFrom(requests)
                .where(requests.user.id.eq(userId)
                        .and(requests.requestType.eq(requestType))
                        .and(requests.requestStatus.ne(requestStatus)))
                .fetch().size();
    }

    public LocalDateTime myRequestsModifiedAt(RequestType requestType, Long userId) {
        QRequests requests = QRequests.requests;
        return queryFactory.select(requests.modifiedAt.max())
                .from(requests).where(requests.requestType.eq(requestType)
                        .and(requests.user.id.eq(userId))).fetchOne();
    }

    public Page<Requests> getRequestsList(String keyword, Set<RequestType> requestTypeQuery,
                                          Set<RequestStatus> requestStatusQuery, Set<Long> userIdQuery, Pageable pageable) {
        QRequests requests = QRequests.requests;
        QUser user = QUser.user;
        QDepartment department = QDepartment.department;
        QCategory category = QCategory.category;
        QSupply supply = QSupply.supply;

        JPAQuery<Requests> query = queryFactory.select(requests).from(requests)
                .innerJoin(requests.user, user)
                .innerJoin(user.department, department)
                .leftJoin(requests.category, category)
                .leftJoin(requests.supply, supply)
                .where(user.empName.containsIgnoreCase(keyword)
                        .or(department.deptName.containsIgnoreCase(keyword))
                        .or(category.categoryName.containsIgnoreCase(keyword))
                        .or(supply.modelName.containsIgnoreCase(keyword))
                        .or(supply.serialNum.containsIgnoreCase(keyword))
                        .and(requests.requestType.in(requestTypeQuery))
                        .and(requests.requestStatus.in(requestStatusQuery)).and(user.id.in(userIdQuery)))
                .orderBy(requests.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<Requests> requestsList = query.fetch();
        long totalCount = query.fetchCount();

        return new PageImpl<>(requestsList, pageable, totalCount);
    }
}
