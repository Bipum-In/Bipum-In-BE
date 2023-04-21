package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.common.enums.SupplyStatusEnum;
import com.sparta.bipuminbe.common.enums.UseType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE supply SET deleted = true WHERE supply_Id = ?")
public class Supply extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplyId;

    @Column(nullable = false, unique = true)
    private String serialNum;

    @Column(nullable = false)
    private String modelName;

    private String image;

    // 비품 상태.
    @Enumerated(EnumType.STRING)
    private SupplyStatusEnum status;

    // 협력 업체(수리 업체)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partnersId")
    private Partners partners;

    // 사용인.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", nullable = false)
    private Category category;

    // 개인/공용
    @Enumerated(EnumType.STRING)
    private UseType useType;

    // 공용일때만 사용하게 된다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(nullable = false)
    private Boolean deleted;


    @Builder
    public Supply(String serialNum, String modelName, String image, SupplyStatusEnum status, Partners partners, User user,
                  Category category, UseType useType, Department department, Boolean deleted) {
        this.serialNum = serialNum;
        this.modelName = modelName;
        this.image = image;
        this.status = status;
        this.partners = partners;
        this.user = user;
        this.category = category;
        this.useType = useType;
        this.department = department;
        this.deleted = deleted;
    }


    // 등록시 등록일자를 기입한 경우.
    public void changeCreatedAt(LocalDateTime createdAt) {
        super.insertCreatedAt(createdAt);
    }


    // 비품 재등록(SoftDelete 된 비품이 재등록 됬을 때 처리.)
    public void reEnroll() {
        this.serialNum = "재등록된 비품#" + supplyId;
        this.modelName = "재등록된 비품#" + supplyId;
    }


    public void update(Partners partners, String image) {
        this.partners = partners;
        this.image = image;
    }


    // 비품 배정.
    public void allocateSupply(Requests request, Department department) {
        if (request.getUseType() == UseType.COMMON) {
            this.department = department;
        } else {
            this.user = request.getUser();
        }
        this.useType = request.getUseType();
        this.status = this.status.equals(SupplyStatusEnum.REPAIRING) ? SupplyStatusEnum.REPAIRING : SupplyStatusEnum.USING;
    }


    // 비품 수리.
    public void repairSupply() {
        status = status.equals(SupplyStatusEnum.REPAIRING)
                ? this.useType == null ? SupplyStatusEnum.STOCK : SupplyStatusEnum.USING
                : SupplyStatusEnum.REPAIRING;
    }


    // 비품 반납.
    public void returnSupply() {
        this.user = null;
        this.department = null;
        this.useType = null;
        this.status = this.status.equals(SupplyStatusEnum.REPAIRING) ? SupplyStatusEnum.REPAIRING : SupplyStatusEnum.STOCK;
    }


    // 협력 업체 삭제.
    public void deletePartners() {
        this.partners = null;
    }
}
