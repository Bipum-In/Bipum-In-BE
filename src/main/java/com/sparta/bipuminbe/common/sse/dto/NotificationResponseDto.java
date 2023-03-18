package com.sparta.bipuminbe.common.sse.dto;

import com.sparta.bipuminbe.common.sse.entity.Notification;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponseDto {
    private String content;
    private String url;

    public static NotificationResponseDto of(Notification notification){

        return builder().
                content(notification.getContent()).
                url(notification.getUrl())
                .build();
    }
}
