package com.sparta.bipuminbe.common.sse.repository;

import com.sparta.bipuminbe.common.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {


    @Query(value = "SELECT * FROM notification WHERE notification.request_type = 'RETURN'", nativeQuery = true)
    List<Notification> findNotification(@Param("userId") Long userId);
}
