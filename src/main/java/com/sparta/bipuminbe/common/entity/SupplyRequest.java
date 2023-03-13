package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.common.dto.SupplyRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class SupplyRequest extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @Column(nullable = false)
    private String content;

    @Column
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplyId", nullable = false)
    private Supply supply;

    private SupplyRequest(SupplyRequestDto supplyRequestDto, User user, Supply supply){
        this.content = supplyRequestDto.getContent();
        this.status = supplyRequestDto.getStatus();
        this.user = user;
        this.supply = supply;
    }


}

