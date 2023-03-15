package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.partners.dto.PartnersDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Partners extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partnersId;

    @Column(nullable = false, unique = true)
    private String partnersName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Builder
    public Partners(PartnersDto partnersDto) {
        this.partnersName = partnersDto.getPartnersName();
        this.phone = partnersDto.getPhone();
        this.address = partnersDto.getAddress();
    }

    public void update(PartnersDto partnersDto) {
        this.partnersName = partnersDto.getPartnersName();
        this.phone = partnersDto.getPhone();
        this.address = partnersDto.getAddress();
    }
}
