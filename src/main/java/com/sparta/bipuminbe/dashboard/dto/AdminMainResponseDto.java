package com.sparta.bipuminbe.dashboard.dto;

import com.sparta.bipuminbe.common.entity.Notification;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AdminMainResponseDto {

    private List<SupplyCountDto> supplyCountDtos;
    private RequestsCountDto requestsCountDto;
//    private List<Notification> notifications;

    public static AdminMainResponseDto of(
            List<SupplyCountDto> supplyCountDtos, RequestsCountDto requestsCountDto
//            List<Notification> notifications
    ) {

        return AdminMainResponseDto.builder()
                .supplyCountDtos(supplyCountDtos)
                .requestsCountDto(requestsCountDto)
//                .notifications(notifications)
                .build();
    }
}
