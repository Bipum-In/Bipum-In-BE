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
    private String image;
    public static NotificationResponseDto of(Notification notification, String image) {

        return builder()
                .content(notification.getContent())
                .url(notification.getUrl())
                .createdAt(notification.getCreatedAt())
                .image(image)
                .build();
    }
}
