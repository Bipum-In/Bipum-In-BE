package com.sparta.bipuminbe.common.queryDSL.requests;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.bipuminbe.common.entity.QRequests;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.RequestType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class RequestsRepositoryImpl implements RequestsRepositoryCustom{
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
}
