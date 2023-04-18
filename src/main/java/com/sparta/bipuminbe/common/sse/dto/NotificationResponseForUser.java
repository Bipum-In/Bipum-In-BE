package com.sparta.bipuminbe.common.sse.dto;

import com.sparta.bipuminbe.common.enums.AcceptResult;
import com.sparta.bipuminbe.common.enums.RequestType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class NotificationResponseForUser {
    private String getContent;
    private LocalDateTime getCreatedAt;
    private AcceptResult getAcceptresult;
    private Long getRequestid;
    private Long getNotificationId;
    private RequestType getRequesttype;
}
