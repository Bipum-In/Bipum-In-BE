package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.common.enums.SupplyStatusEnum;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.supply.dto.SupplyRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.sparta.bipuminbe.common.enums.SupplyStatusEnum.STOCK;
import static com.sparta.bipuminbe.common.enums.SupplyStatusEnum.USING;

@Entity
@Getter
@NoArgsConstructor
//@SQLDelete(sql = "UPDATE Supply SET deleted = true WHERE supplyId = ?")
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

    //Todo Soft Delete 적용 되면 없앨 예정.
    @OneToMany(mappedBy = "supply", cascade = CascadeType.REMOVE)
    private List<Requests> requestsList = new ArrayList<>();

    public Supply(SupplyRequestDto supplyRequestDto, Partners partners, Category category, User user, String image) {
        this.serialNum = supplyRequestDto.getSerialNum();
        this.modelName = supplyRequestDto.getModelName();
        this.image = image;
        this.partners = partners;
        this.status = user == null ? STOCK : USING;
        this.category = category;
        this.user = user;
        this.deleted = false;
    }

    public void allocateSupply(User user) {
        checkSupplyStatus();
        this.user = user;
        this.status = SupplyStatusEnum.USING;
    }

    private void checkSupplyStatus() {
        if (!this.status.equals(STOCK)) {
            throw new CustomException(ErrorCode.NotStockSupply);
        }
    }

    public void repairSupply() {
        status = status.equals(SupplyStatusEnum.REPAIRING) ? SupplyStatusEnum.USING : SupplyStatusEnum.REPAIRING;
    }

    public void returnSupply() {
        this.user = null;
        this.status = STOCK;
    }
}
