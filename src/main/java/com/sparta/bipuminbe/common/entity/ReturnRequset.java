package com.sparta.bipuminbe.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.bipuminbe.common.dto.RetrunRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@DynamicInsert
@NoArgsConstructor
public class ReturnRequset extends TimeStamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long returnId;

    @Column(nullable = false)
    private String content;

    @Column
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplyId")
    private Supply supply;

    private ReturnRequset(RetrunRequestDto retrunRequestDto, User user, Supply supply){
        this.content = retrunRequestDto.getContent();
        this.status = retrunRequestDto.getStatus();
        this.user = user;
        this.supply = supply;
    }

}
