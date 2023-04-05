package com.sparta.bipuminbe.common.sse.dto;

import com.sparta.bipuminbe.common.enums.RequestType;

import java.time.LocalDateTime;


public interface NotificationResponseForAdmin {
    String getContent();
    String getImage();
    LocalDateTime getCreated_At();
    Long getRequest_id();
    Long getNotification_id();
    RequestType getRequest_type();
}
