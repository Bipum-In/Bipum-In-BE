package com.sparta.bipuminbe.partners.dto;

import com.sparta.bipuminbe.common.entity.Partners;
import lombok.Builder;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Builder
@Valid
public class PartnersDto {
    private Long partnersId;
    @NotBlank
    @Pattern(regexp = "[^ㄱ-ㅎㅏ-ㅣ]{1,30}$", message = "한글 자음/모음이 아닌 30자 이내")
    private String partnersName;
    @NotBlank
    private String phone;
    @Email
    private String email;
    @NotBlank
    private String address;

    public static PartnersDto of(Partners partners) {
        return PartnersDto.builder()
                .partnersId(partners.getPartnersId())
                .partnersName(partners.getPartnersName())
                .phone(partners.getPhone())
                .email(partners.getEmail())
                .address(partners.getAddress())
                .build();
    }
}
