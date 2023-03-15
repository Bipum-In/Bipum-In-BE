package com.sparta.bipuminbe.supply.dto;

import com.sparta.bipuminbe.common.entity.Department;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class SupplyResponseDto {

    private Long supplyId;
    private String category;
    private String image;
    private String modelName;
    private String serialNum;
    private LocalDateTime returnDate;
    private LocalDateTime createdAt;
    private String partners;
    private String username;
    private String deptName;
    private String status;

    public SupplyResponseDto(Supply supply, Department department){
        this.supplyId = supply.getSupplyId();
        this.category = supply.getCategory();
        this.image = supply.getImage();
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
