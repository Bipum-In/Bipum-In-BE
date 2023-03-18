package com.sparta.bipuminbe.common.sse.repository;

import com.sparta.bipuminbe.common.sse.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
