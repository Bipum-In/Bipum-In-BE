package com.sparta.bipuminbe.common.queryDSL.notification;

import com.sparta.bipuminbe.common.sse.dto.NotificationResponseForAdmin;
import com.sparta.bipuminbe.common.sse.dto.NotificationResponseForUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface NotificationRepositoryCustom {
    Page<NotificationResponseForAdmin> findUserNotification(Long adminId, Pageable pageable);
    Page<NotificationResponseForUser> findAdminNotification(Long userId, Pageable pageable);
}
