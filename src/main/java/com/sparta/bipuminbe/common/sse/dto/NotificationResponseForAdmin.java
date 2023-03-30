package com.sparta.bipuminbe.common.sse.dto;

import com.sparta.bipuminbe.common.enums.RequestType;

import java.time.LocalDateTime;

public interface NotificationResponseForAdmin {
    String getContent();
    LocalDateTime getCreated_At();
    String getImage();
    Long getRequest_id();
    RequestType getRequest_type();
}
