package com.sparta.bipuminbe.supply.dto;

import com.sparta.bipuminbe.common.entity.Supply;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class SupplyRepairHistoryResponseDto {

    private LocalDateTime modifiedAt;
//    private String username;
    private String empName;
    private String deptName;
    private String partnersName;

    public SupplyRepairHistoryResponseDto(Supply supply){
        this.modifiedAt = supply.getModifiedAt();
//        this.username = supply.getUser().getUsername();
        this.empName = supply.getUser() == null ? null : supply.getUser().getEmpName();
        this.deptName = supply.getUser() == null ? null : supply.getUser().getDepartment().getDeptName();
        this.partnersName = supply.getPartners() == null ? null : supply.getPartners().getPartnersName();
    }
}
