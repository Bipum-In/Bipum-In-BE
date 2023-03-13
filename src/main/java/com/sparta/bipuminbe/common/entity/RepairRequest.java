package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.requests.dto.RepairRequestDto;
import com.sparta.bipuminbe.requests.dto.SupplyRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class RepairRequest extends TimeStamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private Boolean isSelf;

    @Column(nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUPPLY_ID")
    private Supply supply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Builder
    public RepairRequest(RepairRequestDto repairRequestDto, Supply supply, User user){
        this.content = repairRequestDto.getContent();
        this.image = repairRequestDto.getImage();
        this.isSelf = repairRequestDto.getIsSelf();
        this.status = repairRequestDto.getStatus();
        this.supply = supply;
        this.user = user;
    }


}
