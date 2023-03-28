package com.sparta.bipuminbe.common.entity;

import com.sparta.bipuminbe.partners.dto.PartnersDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE partners SET deleted = true WHERE partners_id = ?")
//@Where(clause = "deleted = false")  // 조회할 때 false만 찾는 것이 default 가 된다.
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

    @Column(nullable = false)
    private Boolean deleted;

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
