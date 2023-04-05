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
    public Boolean verifiedEmail;

    public String name;
    public String givenName;
    public String familyName;
    public String picture;
    public String locale;
}
