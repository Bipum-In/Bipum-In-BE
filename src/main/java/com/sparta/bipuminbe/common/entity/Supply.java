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
//@Where(clause = "deleted = false")  // 조회할 때 false만 찾는 것이 default 가 된다.
public class Supply extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplyId;

    @Column(nullable = false, unique = true)
    private String serialNum;

    @Column(nullable = false)
    private String modelName;

    private String image;

    @Enumerated(EnumType.STRING)
    private SupplyStatusEnum status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partnersId")
    private Partners partners;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", nullable = false)
    private Category category;

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
                  Category category, UseType useType, Department department, Boolean deleted, LocalDateTime createdAt) {
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
        super.insertCreatedAt(createdAt);
    }

    public void update(Partners partners, String image) {
        this.partners = partners;
        this.image = image;
    }

    public void allocateSupply(Requests request, Department department) {
//        checkSupplyStatus();
        if (request.getUseType() == UseType.COMMON) {
            this.department = department;
        } else {
            this.user = request.getUser();
        }
        this.useType = request.getUseType();
        this.status = this.status.equals(SupplyStatusEnum.REPAIRING) ? SupplyStatusEnum.REPAIRING : SupplyStatusEnum.USING;
    }

//    private void checkSupplyStatus() {
//        if (this.status == SupplyStatusEnum.USING) {
//            throw new CustomException(ErrorCode.NotStockSupply);
//        }
//    }

    public void repairSupply() {
        status = status.equals(SupplyStatusEnum.REPAIRING)
                ? this.useType == null ? SupplyStatusEnum.STOCK : SupplyStatusEnum.USING
                : SupplyStatusEnum.REPAIRING;
    }

    public void returnSupply() {
        this.user = null;
        this.department = null;
        this.useType = null;
        this.status = this.status.equals(SupplyStatusEnum.REPAIRING) ? SupplyStatusEnum.REPAIRING : SupplyStatusEnum.STOCK;
    }

    public void deletePartners() {
        this.partners = null;
    }
}
