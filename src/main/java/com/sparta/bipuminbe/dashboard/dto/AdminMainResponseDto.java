package com.sparta.bipuminbe.dashboard.dto;

import com.sparta.bipuminbe.common.sse.dto.NotificationResponseForAdmin;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AdminMainResponseDto {

    private List<SupplyCountDto> supplyCountDtos;
    private RequestsCountDto requestsCountDto;
//    private List<NotificationResponseForAdmin> notifications;

    public static AdminMainResponseDto of(
            List<SupplyCountDto> supplyCountDtos, RequestsCountDto requestsCountDto
//            List<NotificationResponseForAdmin> notifications
    ) {

        return AdminMainResponseDto.builder()
                .supplyCountDtos(supplyCountDtos)
                .requestsCountDto(requestsCountDto)
//                .notifications(notifications)
                .build();
    }
}
