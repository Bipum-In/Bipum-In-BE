package com.sparta.bipuminbe.common.sse.dto;

import com.sparta.bipuminbe.common.enums.RequestType;

import java.time.LocalDateTime;


public interface NotificationResponseForAdmin {
    String getContent();
    String getImage();
    LocalDateTime getCreatedAt();
    Long getRequestId();
    Long getNotificationId();
    RequestType getRequestType();
}
