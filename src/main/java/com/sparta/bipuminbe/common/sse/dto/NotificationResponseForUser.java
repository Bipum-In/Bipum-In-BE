package com.sparta.bipuminbe.common.sse.dto;

import com.sparta.bipuminbe.common.enums.AcceptResult;

import java.time.LocalDateTime;

public interface NotificationResponseForUser {
    String getContent();

    String getUrl();
    LocalDateTime getCreated_At();

    AcceptResult getAccept_result();
}
