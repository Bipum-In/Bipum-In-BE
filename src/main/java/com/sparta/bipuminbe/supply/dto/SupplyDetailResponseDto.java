package com.sparta.bipuminbe.supply.dto;

import com.sparta.bipuminbe.common.entity.Supply;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class SupplyDetailResponseDto {

    private Long supplyId;
    private String image;
    private String category;
    private String modelName;
    private String serialNum;
    private LocalDateTime createdAt;
    private String partnersName;
    private String empName;
    private String deptName;
    private String status;

    public SupplyDetailResponseDto(Supply supply){
        this.supplyId = supply.getSupplyId();
        this.image = supply.getImage();
        this.category = supply.getCategory().getCategoryName();
        this.modelName = supply.getModelName();
        this.serialNum = supply.getSerialNum();
        this.createdAt = supply.getCreatedAt();
        this.partnersName = supply.getPartners() == null ? null : supply.getPartners().getPartnersName();
        this.empName = supply.getUser() == null ? null : supply.getUser().getEmpName();
        this.deptName = supply.getUser() == null ? null : supply.getUser().getDepartment().getDeptName();
        this.status = supply.getStatus().name();
    }
}
