package com.sparta.bipuminbe.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity(name = "repairRequest")
@NoArgsConstructor
public class RepairRequest extends TimeStamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    private Supply supply;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;


}
