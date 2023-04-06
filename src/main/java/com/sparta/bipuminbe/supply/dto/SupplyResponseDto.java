package com.sparta.bipuminbe.supply.dto;

import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.UseType;
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
    private String partners;
    private String empName;
    private String deptName;
    private String status;
    private LocalDateTime createdAt;

    public SupplyResponseDto(Supply supply){
        UseType useType = supply.getUseType();
        User user = supply.getUser();

        this.supplyId = supply.getSupplyId();
        this.category = supply.getCategory().getCategoryName();
        this.image = supply.getImage();
        this.modelName = supply.getModelName();
        this.serialNum = supply.getSerialNum();
        this.createdAt = supply.getCreatedAt();
        this.partners = supply.getPartners() == null ? null : supply.getPartners().getPartnersName();
        this.empName = useType == UseType.COMMON ? useType.getKorean() : user == null ? null : user.getEmpName();
        this.deptName = useType == UseType.COMMON ? supply.getDepartment().getDeptName() :
                user == null ? null : user.getDepartment().getDeptName();
        this.status = supply.getStatus().getKorean();
    }
    public static SupplyResponseDto of (Supply supply) {
        return new SupplyResponseDto(supply);
    }
}
