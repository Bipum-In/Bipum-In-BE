package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.common.enums.SupplyStatusEnum;
import com.sparta.bipuminbe.supply.dto.SupplyRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE supply SET deleted = true WHERE supply_Id = ?")
//@Where(clause = "deleted = false")  // 조회할 때 false만 찾는 것이 default 가 된다.
public class Supply extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplyId;

    @Column(nullable = false)
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

    @Column(nullable = false)
    private Boolean deleted;

    public Supply(SupplyRequestDto supplyRequestDto, Partners partners, Category category, User user, String image) {
        this.serialNum = supplyRequestDto.getSerialNum();
        this.modelName = supplyRequestDto.getModelName();
        this.image = image;
        this.partners = partners;
        this.status = user == null ? SupplyStatusEnum.STOCK : SupplyStatusEnum.USING;
        this.category = category;
        this.user = user;
        this.deleted = false;
    }

    public void update(Partners partners, User user, String image) {
        this.partners = partners;
        this.user = user;
        this.image = image;
    }

    public void allocateSupply(User user) {
//        checkSupplyStatus();
        this.user = user;
        this.status = this.status.equals(SupplyStatusEnum.REPAIRING) ? SupplyStatusEnum.REPAIRING : SupplyStatusEnum.USING;
    }

//    private void checkSupplyStatus() {
//        if (this.status == SupplyStatusEnum.USING) {
//            throw new CustomException(ErrorCode.NotStockSupply);
//        }
//    }

    public void repairSupply() {
        status = status.equals(SupplyStatusEnum.REPAIRING)
                ? this.user == null ? SupplyStatusEnum.STOCK : SupplyStatusEnum.USING
                : SupplyStatusEnum.REPAIRING;
    }

    public void returnSupply() {
        this.user = null;
        this.status = this.status.equals(SupplyStatusEnum.REPAIRING) ? SupplyStatusEnum.REPAIRING : SupplyStatusEnum.STOCK;
    }

    public void deletePartners() {
        this.partners = null;
    }
}
