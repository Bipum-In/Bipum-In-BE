package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.requests.dto.RepairRequestDto;
import com.sparta.bipuminbe.requests.dto.RetrunRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class ReturnRequest extends TimeStamped {

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
    public ReturnRequest(RetrunRequestDto retrunRequestDto, Supply supply, User user){
        this.content = retrunRequestDto.getContent();
        this.status = retrunRequestDto.getStatus();
        this.supply = supply;
        this.user = user;
    }


}