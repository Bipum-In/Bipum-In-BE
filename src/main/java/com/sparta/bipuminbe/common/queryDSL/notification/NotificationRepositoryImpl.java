package com.sparta.bipuminbe.common.queryDSL.notification;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.bipuminbe.common.entity.Notification;
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
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    // notification의 request_id 와 같은 요청과 그 요청에 해당하는 관리자의 정보를 가져온다 (승인/폐기 등 처리 건만)
    // 요청의 수신자가 userId에 해당하는 알림 건만 가져온다.
    public Page<NotificationResponseForAdmin> findUserNotification(Long adminId, Pageable pageable) {
        QNotification notification = QNotification.notification;
        QRequests request = QRequests.requests;
        QUser user = QUser.user;

        JPAQuery<NotificationResponseForAdmin> query = jpaQueryFactory.select(Projections.constructor(
                        NotificationResponseForAdmin.class,
                        notification.content, user.image, notification.createdAt.as("createdAt"),
                        request.requestId.as("requestId"), notification.id.as("notificationId"), request.requestType))
                .from(notification)
                .join(request).on(notification.request.requestId.eq(request.requestId))
                .join(user).on(request.user.id.eq(user.id))
                .where(notification.receiver.id.eq(adminId)
                        .and(notification.notificationType.eq(NotificationType.REQUEST))
                        .and(notification.isRead.eq(false)))
                .orderBy(notification.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<NotificationResponseForAdmin> notificationList = query.fetch();
        long totalCount = query.fetchCount();

        return new PageImpl<>(notificationList, pageable, totalCount);
    }

    // notification의 requests_id 와 같은 요청과 그 요청에 해당하는 유저의 정보를 가져온다 (요청 건만)
    // 요청의 수신자가 adminId에 해당하는 알림 건만 가져온다.
    public Page<NotificationResponseForUser> findAdminNotification(Long userId, Pageable pageable) {
        QNotification notification = QNotification.notification;
        QRequests requests = QRequests.requests;
        QUser user = QUser.user;

        JPAQuery<NotificationResponseForUser> query = jpaQueryFactory.select(Projections.constructor(
                                NotificationResponseForUser.class,
                                notification.content, notification.createdAt.as("createdAt"), notification.acceptResult,
                                requests.requestId.as("requestId"), notification.id.as("notificationId"), requests.requestType
                        )
                )
                .from(notification)
                .join(requests).on(notification.request.requestId.eq(requests.requestId))
                .join(user).on(requests.admin.id.eq(user.id))
                .where(notification.receiver.id.eq(userId)
                        .and(notification.notificationType.eq(NotificationType.PROCESSED))
                        .and(notification.isRead.eq(false)))
                .orderBy(notification.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<NotificationResponseForUser> notificationList = query.fetch();
        long totalCount = query.fetchCount();

        return new PageImpl<>(notificationList, pageable, totalCount);
    }

    public List<Notification> findOldNotification() {
        QNotification notification = QNotification.notification;
        BooleanExpression isRead = notification.isRead.eq(true);
        BooleanExpression createdAt = notification.createdAt.before(LocalDateTime.now().minusDays(1));
        BooleanExpression now = Expressions.asBoolean(true);

        return jpaQueryFactory.selectFrom(notification)
                .where(isRead, createdAt, now)
                .fetch();
    }
}
