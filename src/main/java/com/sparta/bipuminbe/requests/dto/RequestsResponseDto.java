package com.sparta.bipuminbe.requests.dto;

import com.sparta.bipuminbe.common.entity.RepairRequest;
import com.sparta.bipuminbe.common.entity.SupplyRequest;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.RequestType;
import com.sparta.bipuminbe.common.exception.CustomException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class RequestsResponseDto implements Comparable<RequestsResponseDto> {
    private String type;
    private Long requestId;
    private String empName;
    private String deptName;
    private String categoryName;
    private String modelName;
    private LocalDateTime createdAt;
    private String status;

    @Override
    public int compareTo(RequestsResponseDto o) {
        return this.createdAt.compareTo(o.createdAt);
    }
}
