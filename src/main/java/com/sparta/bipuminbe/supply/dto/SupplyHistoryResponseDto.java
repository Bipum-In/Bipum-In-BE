package com.sparta.bipuminbe.supply.dto;

import com.sparta.bipuminbe.common.entity.Supply;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class SupplyHistoryResponseDto {

    private LocalDateTime createdAt;
    private String username;
    private String status;

    public SupplyHistoryResponseDto(Supply supply){
        this.createdAt = supply.getCreatedAt();
        this.username = supply.getUser().getUsername();
        this.status = supply.getStatus().name();
    }
}
