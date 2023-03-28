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
    private byte[] imageBytes;
    public static NotificationResponseDto of(Notification notification, byte[] imageBytes) {

        return builder()
                .content(notification.getContent())
                .url(notification.getUrl())
                .createdAt(notification.getCreatedAt())
                .imageBytes(imageBytes)
                .build();
    }
}
