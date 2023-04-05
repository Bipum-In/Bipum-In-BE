package com.sparta.bipuminbe.common.sse.dto;

import com.sparta.bipuminbe.common.enums.AcceptResult;
import com.sparta.bipuminbe.common.enums.RequestType;

import java.time.LocalDateTime;

public interface NotificationResponseForUser {
    String getContent();
    LocalDateTime getCreated_At();
    AcceptResult getAccept_result();
    Long getRequest_id();
    Long getNotification_id();
    RequestType getRequest_type();
}
