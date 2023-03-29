package com.sparta.bipuminbe.supply.dto;

import com.sparta.bipuminbe.common.entity.Partners;
import com.sparta.bipuminbe.common.entity.Supply;
import com.sparta.bipuminbe.common.entity.User;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class SupplyDetailResponseDto {

    private Long supplyId;
    private Boolean isAdmin;
    private String image;
    private String largeCategory;
    private String category;
    private String modelName;
    private String serialNum;
    private LocalDateTime createdAt;
    private Long partnersId;
    private String partnersName;
    private Long userId;
    private String empName;
    private String deptName;
    private String status;

    public SupplyDetailResponseDto(Supply supply, UserRoleEnum role) {
        Partners partners = supply.getPartners();
        User user = supply.getUser();

        this.supplyId = supply.getSupplyId();
        this.isAdmin = role.equals(UserRoleEnum.ADMIN);
        this.image = supply.getImage();
        this.largeCategory = supply.getCategory().getLargeCategory().getKorean();
        this.category = supply.getCategory().getCategoryName();
        this.modelName = supply.getModelName();
        this.serialNum = supply.getSerialNum();
        this.createdAt = supply.getCreatedAt();
        this.partnersId = partners == null ? null : partners.getPartnersId();
        this.partnersName = partners == null ? null : partners.getPartnersName();
        this.userId = user == null ? null : user.getId();
        this.empName = user == null ? null : user.getEmpName();
        this.deptName = user == null ? null : user.getDepartment().getDeptName();
        this.status = supply.getStatus().name();
    }
}
