package com.sparta.bipuminbe.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Partners {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partnersId;

    @Column(nullable = false)
    private String partnersName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;
}
