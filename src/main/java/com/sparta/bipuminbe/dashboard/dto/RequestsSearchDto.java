package com.sparta.bipuminbe.dashboard.dto;

import lombok.Getter;

@Getter
public class RequestsSearchDto {
    private Long requestId;
    private String requestType;
    private String categoryName;
    private String modelName;
    private String deptName;
    private String empName;
    private String status;
    private String acceptResult;
}
