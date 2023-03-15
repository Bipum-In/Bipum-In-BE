package com.sparta.bipuminbe.supply.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SupplyRequestDto {

    private String category;
    private String modelName;
    private String serialNum;
    private LocalDateTime returnDate;
    private String partners;
    private Long userId;
}
