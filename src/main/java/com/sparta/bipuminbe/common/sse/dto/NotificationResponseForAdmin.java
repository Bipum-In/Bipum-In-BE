package com.sparta.bipuminbe.common.sse.dto;

import com.sparta.bipuminbe.common.enums.RequestType;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
public class NotificationResponseForAdmin {
    private String getContent;
    private String getImage;
    private LocalDateTime getCreatedAt;
    private Long getRequestId;
    private Long getNotificationId;
    private RequestType getRequestType;
}
