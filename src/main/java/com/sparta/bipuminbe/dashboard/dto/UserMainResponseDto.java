package com.sparta.bipuminbe.dashboard.dto;

import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.sse.dto.NotificationResponseForUser;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserMainResponseDto {
    private List<UserSupplyDto> userSupplyDtos;
    private RequestsCountDto requestsCountDto;
//    private List<NotificationResponseForUser> notifications;

    public static UserMainResponseDto of(List<UserSupplyDto> userSupplyDtos, RequestsCountDto requestsCountDto
//                                         List<NotificationResponseForUser> notifications
    ) {
        return builder()
                .userSupplyDtos(userSupplyDtos)
                .requestsCountDto(requestsCountDto)
//                .notifications(notifications)
                .build();
    }
}
