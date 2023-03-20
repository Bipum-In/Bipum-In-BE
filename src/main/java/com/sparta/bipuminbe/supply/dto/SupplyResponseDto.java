package com.sparta.bipuminbe.supply.dto;

import com.sparta.bipuminbe.common.entity.Supply;
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
    private LocalDateTime createdAt;
    private String partners;
    private String username;
    private String deptName;
    private String status;

    public SupplyResponseDto(Supply supply){
        this.supplyId = supply.getSupplyId();
        this.category = supply.getCategory().getCategoryName();
        this.image = supply.getImage();
        this.modelName = supply.getModelName();
        this.serialNum = supply.getSerialNum();
        this.createdAt = supply.getCreatedAt();
        this.partners = supply.getPartners() == null ? null : supply.getPartners().getPartnersName();
        this.username = supply.getUser() == null ? null : supply.getUser().getEmpName();
        this.deptName = supply.getUser() == null ? null : supply.getUser().getDepartment().getDeptName();
        this.status = supply.getStatus().name();
    }
    public static SupplyResponseDto of (Supply supply) {
        return new SupplyResponseDto(supply);
    }
}
