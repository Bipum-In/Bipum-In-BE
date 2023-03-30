package com.sparta.bipuminbe.dashboard.dto;

import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.sse.dto.NotificationResponseForUser;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class UserMainResponseDto {
    private List<UserSupplyDto> userSupplyDtos;
    private Map<String, Long> userCountMap;
    private List<NotificationResponseForUser> notifications;

    public static UserMainResponseDto of(List<UserSupplyDto> userSupplyDtos, Map<String, Long> userCountMap,
                                         List<NotificationResponseForUser> notifications) {
        return builder()
                .userSupplyDtos(userSupplyDtos)
                .userCountMap(userCountMap)
                .notifications(notifications)
                .build();
    }
}
