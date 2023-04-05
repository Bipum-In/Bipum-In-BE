package com.sparta.bipuminbe.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class GoogleUserInfoDto {
    public String id;
    public String email;
    public Boolean verified_email;

    public String name;
    public String given_name;
    public String family_name;
    public String picture;
    public String locale;
}
