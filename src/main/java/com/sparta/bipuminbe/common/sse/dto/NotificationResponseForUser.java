package com.sparta.bipuminbe.common.sse.dto;

import com.sparta.bipuminbe.common.enums.AcceptResult;
import com.sparta.bipuminbe.common.enums.RequestType;

import java.time.LocalDateTime;

public interface NotificationResponseForUser {
    String getContent();
    LocalDateTime getCreatedAt();
    AcceptResult getAcceptresult();
    Long getRequestid();
    Long getNotificationId();
    RequestType getRequesttype();
}
