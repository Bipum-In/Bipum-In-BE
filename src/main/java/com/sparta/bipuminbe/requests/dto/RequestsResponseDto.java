package com.sparta.bipuminbe.requests.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RequestsResponseDto {
    private Long requestsId;
    private String message;

}
