package com.sparta.bipuminbe.common.queryDSL.notification;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.bipuminbe.common.entity.QNotification;
import com.sparta.bipuminbe.common.entity.QRequests;
import com.sparta.bipuminbe.common.entity.QUser;
import com.sparta.bipuminbe.common.enums.NotificationType;
import com.sparta.bipuminbe.common.sse.dto.NotificationResponseForAdmin;
import com.sparta.bipuminbe.common.sse.dto.NotificationResponseForUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    // 기존에 join한 데이터를 받아오는 방법을 어떻게 변경할 것인지가 관건
    public Page<NotificationResponseForAdmin> findUserNotification(Long adminId, Pageable pageable) {
//        QNotification notification = QNotification.notification;
//        QRequests request = QRequests.requests;
//        QUser user = QUser.user;
//
//        JPAQuery<Tuple> query = jpaQueryFactory.select(notification.content, notification.createdAt, notification.acceptResult, request.requestId, notification.id, request.requestType)
//                .from(notification)
//                .join(notification.request, request)
//                .join(request.user, user)
//                .where(notification.receiver.id.eq(adminId)
//                        .and(notification.notificationType.eq(NotificationType.REQUEST))
//                        .and(notification.isRead.eq(false)))
//                .orderBy(notification.createdAt.desc());
//
//        return PageableExecutionUtils.getPage(query.fetch(), pageable, )
        return null;
    }

    public Page<NotificationResponseForUser> findAdminNotification(Long userId, Pageable pageable) {
        return null;
    }
}
