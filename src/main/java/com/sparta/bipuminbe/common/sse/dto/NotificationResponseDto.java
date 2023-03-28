package com.sparta.bipuminbe.common.sse.dto;

import com.sparta.bipuminbe.common.sse.entity.Notification;
import lombok.Builder;
import lombok.Getter;
import org.joda.time.DateTime;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponseDto {
    private String content;
    private String url;
    private LocalDateTime createdAt;

    public static NotificationResponseDto of(Notification notification) {

        return builder()
                .content(notification.getContent())
                .url(notification.getUrl())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
