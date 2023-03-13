package com.sparta.bipuminbe.common.entity;

//import com.sparta.bipuminbe.common.dto.SupplyRequestDto;
import com.sparta.bipuminbe.requests.dto.SupplyRequestDto;
import lombok.Builder;
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

    @Builder
    public SupplyRequest(SupplyRequestDto supplyRequestDto, Supply supply, User user){
        this.content = supplyRequestDto.getContent();
        this.status = supplyRequestDto.getStatus();
        this.supply = supply;
        this.user = user;
    }
}

