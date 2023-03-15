package com.sparta.bipuminbe.supply.dto;

import com.sparta.bipuminbe.common.entity.Department;
import com.sparta.bipuminbe.common.entity.Supply;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class SupplyDetailResponseDto {

    private String category;
    private String modelName;
    private String serialNum;
    private LocalDateTime returnDate;
    private LocalDateTime createdAt;
    private String partners;
    private String username;
    private String deptName;
    private String status;

    public SupplyDetailResponseDto(Supply supply, Department department){
        this.category = supply.getCategory();
        this.modelName = supply.getModelName();
        this.serialNum = supply.getSerialNum();
        this.returnDate = supply.getReturnDate();
        this.createdAt = supply.getCreateAt();
        this.partners = supply.getPartners();
        this.username = supply.getUser.getUsername();
        this.deptName = department.getDeptName();
        this.status = supply.getStatus();
    }
}
