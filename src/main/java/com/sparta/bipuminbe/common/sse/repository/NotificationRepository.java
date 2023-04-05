package com.sparta.bipuminbe.common.sse.repository;

import com.sparta.bipuminbe.common.entity.Notification;
import com.sparta.bipuminbe.common.sse.dto.NotificationResponseForAdmin;
import com.sparta.bipuminbe.common.sse.dto.NotificationResponseForUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // notification의 requests_id 와 같은 요청과 그 요청에 해당하는 유저의 정보를 가져온다 (요청 건만)
    // 요청의 수신자가 adminId에 해당하는 알림 건만 가져온다.
    @Query(value = "SELECT n.content, u.image, n.created_at, r.request_id, n.notification_id, r.request_type FROM notification n " +
            "INNER JOIN requests r ON n.requests_id = r.request_id " +
            "INNER JOIN users u ON r.user_id = u.id " +
            "WHERE n.receiver_id = :adminId AND n.notification_type = 'REQUEST' AND n.is_read = 'false'" +
            "ORDER BY n.created_at DESC", nativeQuery = true)
    Page<NotificationResponseForAdmin> findUserNotification(@Param("adminId") Long adminId, Pageable pageable);


    // notification의 request_id 와 같은 요청과 그 요청에 해당하는 관리자의 정보를 가져온다 (승인/폐기 등 처리 건만)
    // 요청의 수신자가 userId에 해당하는 알림 건만 가져온다.
    @Query(value = "SELECT n.content, n.created_at, n.accept_result, r.request_id, n.notification_id, r.request_type FROM notification n " +
            "INNER JOIN requests r ON n.requests_id = r.request_id " +
            "INNER JOIN users u ON r.user_id = u.id " +
            "WHERE n.receiver_id = :userId AND n.notification_type = 'PROCESSED' AND n.is_read = 'false'" +
            "ORDER BY n.created_at DESC", nativeQuery = true)
    Page<NotificationResponseForUser> findAdminNotification(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT * FROM notification n " +
            "WHERE n.created_at < date_add(now(), INTERVAL -1 DAY) " +
            "AND n.is_read = 'true' AND NOW()", nativeQuery = true)
    List<Notification> findOldNotification();
}