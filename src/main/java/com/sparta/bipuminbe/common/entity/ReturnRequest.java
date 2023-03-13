package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.common.dto.RetrunRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class ReturnRequest extends TimeStamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplyId", nullable = false)
    private Supply supply;

    private ReturnRequest(RetrunRequestDto retrunRequestDto, User user, Supply supply){
        this.content = retrunRequestDto.getContent();
        this.status = retrunRequestDto.getStatus();
        this.user = user;
        this.supply = supply;
    }

}
