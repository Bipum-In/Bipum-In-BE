package com.sparta.bipuminbe.common.sse.dto;

import java.time.LocalDateTime;

public interface NotificationResponseForAdmin {
    String getContent();

    String getUrl();
    LocalDateTime getCreated_At();

    String getImage();
}
