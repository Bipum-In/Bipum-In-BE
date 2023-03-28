package com.sparta.bipuminbe.common.sse.repository;

import com.sparta.bipuminbe.common.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findTop4ByReceiver_idOrderByCreatedAtDesc(Long userId);
}
