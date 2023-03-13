package com.sparta.bipuminbe.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity(name = "category")
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "supply")
    List<Supply> supplyList = new ArrayList<>();

    @OneToMany(mappedBy = "supplyRequest")
    List<SupplyRequest> supplyRequestList = new ArrayList<>();
}
