package com.sparta.bipuminbe.common.sse.dto;

import com.sparta.bipuminbe.common.entity.Notification;
import com.sparta.bipuminbe.common.enums.AcceptResult;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.RequestType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponseDto {
    private Long notificationId;
    private String content;
    private String url;
    private LocalDateTime createdAt;
    private String image;
    private Long requestId;
    private RequestType requestType;
    private AcceptResult acceptResult;
    private boolean isRead;

    public static NotificationResponseDto of(Notification notification, String image) {

        return builder()
                .notificationId(notification.getId())
                .content(notification.getContent())
                .createdAt(notification.getCreatedAt())
                .requestId(notification.getRequest().getRequestId())
                .requestType(notification.getRequest().getRequestType())
                .image(image)
                .acceptResult(notification.getAcceptResult())
                .isRead(notification.getIsRead())
                .build();
    }
}
