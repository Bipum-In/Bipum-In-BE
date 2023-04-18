package com.sparta.bipuminbe.common.sse.dto;

import com.sparta.bipuminbe.common.enums.RequestType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
public class NotificationResponseForAdmin {
    private String getContent;
    private String getImage;
    private LocalDateTime getCreatedAt;
    private Long getRequestId;
    private Long getNotificationId;
    private RequestType getRequestType;
}
