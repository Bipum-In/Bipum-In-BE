package com.sparta.bipuminbe.common.sse.repository;

import com.sparta.bipuminbe.common.entity.Notification;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.NotificationType;
import com.sparta.bipuminbe.common.queryDSL.notification.NotificationRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {

    long countByReceiver_IdAndNotificationTypeAndIncludeCountTrue(Long id, NotificationType notificationType);

    List<Notification> findByReceiver_IdAndNotificationTypeAndIncludeCountTrue(Long id, NotificationType notificationType);

    List<Notification> findByRequest_RequestId(Long requestId);

    List<Notification> findByReceiverAndNotificationType(User user, NotificationType notificationType);
}