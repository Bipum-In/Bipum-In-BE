package com.sparta.bipuminbe.partners.dto;

import com.sparta.bipuminbe.common.entity.Partners;
import lombok.Builder;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@Valid
public class PartnersDto {
    private Long partnersId;
    @NotNull
    private String partnersName;
    private String phone;
    private String address;

    public static PartnersDto of(Partners partners) {
        return PartnersDto.builder()
                .partnersId(partners.getPartnersId())
                .partnersName(partners.getPartnersName())
                .phone(partners.getPhone())
                .address(partners.getAddress())
                .build();
    }
}
