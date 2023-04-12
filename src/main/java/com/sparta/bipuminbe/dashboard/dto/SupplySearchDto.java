package com.sparta.bipuminbe.dashboard.dto;

import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.UseType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SupplySearchDto {
    private Long supplyId;
    private String image;
    private String categoryName;
    private String modelName;
    private String serialNum;
    private String deptName;
    private String empName;
    private String status;

    public static SupplySearchDto of(Supply supply) {
        User user = supply.getUser();

        return SupplySearchDto.builder()
                .supplyId(supply.getSupplyId())
                .image(supply.getImage())
                .categoryName(supply.getCategory().getCategoryName())
                .modelName(supply.getModelName())
                .serialNum(supply.getSerialNum())
                .deptName(supply.getUseType() == UseType.COMMON ? supply.getDepartment().getDeptName() :
                        supply.getUseType() == UseType.PERSONAL ? user.getDepartment().getDeptName() : null)
                .empName(supply.getUseType() == UseType.COMMON ? supply.getUseType().getKorean() :
                        supply.getUseType() == UseType.PERSONAL ? user.getEmpName() : null)
                .status(supply.getStatus().getKorean())
                .build();
    }
}
