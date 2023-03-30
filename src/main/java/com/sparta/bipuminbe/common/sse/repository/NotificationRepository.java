package com.sparta.bipuminbe.common.sse.repository;

import com.sparta.bipuminbe.common.entity.Notification;
import com.sparta.bipuminbe.common.sse.dto.NotificationResponseForAdmin;
import com.sparta.bipuminbe.common.sse.dto.NotificationResponseForUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // notification의 requests_id 와 같은 요청과 그 요청에 해당하는 유저의 정보를 가져온다 (요청 건만)
    // 요청의 수신자가 adminId에 해당하는 알림 건만 가져오고, 최신 순으로 4건 가져온다.
    @Query(value = "SELECT n.content, n.url, n.created_at, u.image FROM notification n " +
            "INNER JOIN requests r ON n.requests_id = r.request_id " +
            "INNER JOIN users u ON r.user_id = u.id " +
            "WHERE n.receiver_id = :adminId AND n.notification_type = 'REQUEST'" +
            "ORDER BY n.created_at DESC LIMIT 0,4", nativeQuery = true)
    List<NotificationResponseForAdmin> findUserNotification(@Param("adminId") Long adminId);


    // notification의 request_id 와 같은 요청과 그 요청에 해당하는 관리자의 정보를 가져온다 (승인/폐기 등 처리 건만)
    // 요청의 수신자가 userId에 해당하는 알림 건만 가져오고, 최신 순으로 4건 가져온다.
    @Query(value = "SELECT n.content, n.url, n.created_at, r.accept_result FROM notification n " +
            "INNER JOIN requests r ON n.requests_id = r.request_id " +
            "INNER JOIN users u ON r.user_id = u.id " +
            "WHERE n.receiver_id = :userId AND n.notification_type = 'PROCESSED'" +
            "ORDER BY n.created_at DESC LIMIT 0,4", nativeQuery = true)
    List<NotificationResponseForUser> findAdminNotification(@Param("userId") Long userId);
}