package com.sparta.bipuminbe.common.queryDSL.notification;

import com.sparta.bipuminbe.common.entity.Notification;
import com.sparta.bipuminbe.common.sse.dto.NotificationResponseForAdmin;
import com.sparta.bipuminbe.common.sse.dto.NotificationResponseForUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationRepositoryCustom {
    Page<NotificationResponseForAdmin> findUserNotification(Long adminId, Pageable pageable);
    Page<NotificationResponseForUser> findAdminNotification(Long userId, Pageable pageable);
    List<Notification> findOldNotification();
}
