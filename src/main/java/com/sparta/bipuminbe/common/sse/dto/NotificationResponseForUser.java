package com.sparta.bipuminbe.common.sse.dto;

import com.sparta.bipuminbe.common.enums.AcceptResult;
import com.sparta.bipuminbe.common.enums.RequestType;
import lombok.AllArgsConstructor;
import lombok.Getter;


import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class NotificationResponseForUser {
    private String getContent;
    private LocalDateTime getCreatedAt;
    private AcceptResult getAcceptresult;
    private Long getRequestId;
    private Long getNotificationId;
    private RequestType getRequesttype;
}
