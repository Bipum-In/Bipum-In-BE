package com.sparta.bipuminbe.common.sse.dto;

import com.sparta.bipuminbe.common.entity.Notification;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponseDto {
    private String content;
    private String url;
    private LocalDateTime createdAt;
    private String encodeToString;
    public static NotificationResponseDto of(Notification notification, String encodeToString) {

        return builder()
                .content(notification.getContent())
                .url(notification.getUrl())
                .createdAt(notification.getCreatedAt())
                .encodeToString(encodeToString)
                .build();
    }
}
