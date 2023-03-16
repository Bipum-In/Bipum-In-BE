package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.common.enums.SupplyStatusEnum;
import com.sparta.bipuminbe.supply.dto.SupplyRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Supply extends TimeStamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplyId;

    @Column(nullable = false)
    private String serialNum;

    @Column(nullable = false)
    private String modelName;

    @Column(nullable = false)
    private String image;

    @Enumerated(EnumType.STRING)
    private SupplyStatusEnum status;

    private LocalDateTime returnDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partnersId")
    private Partners partners;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", nullable = false)
    private Category category;

    public Supply(SupplyRequestDto supplyRequestDto, Partners partners) {
        this.serialNum = supplyRequestDto.getSerialNum();
        this.modelName = supplyRequestDto.getModelName();
        this.image = "";
        this.partners = partners;
        this.status = SupplyStatusEnum.STOCK;
        this.returnDate = supplyRequestDto.getReturnDate();
    }

    public void allocateSupply(User user) {
        this.user = user;
        this.status = SupplyStatusEnum.USING;
    }

    public void repairSupply() {
        this.status = SupplyStatusEnum.REPAIRING;
    }

    public void returnSupply() {
        this.user = null;
        this.status = SupplyStatusEnum.STOCK;
    }
}
