package com.sparta.bipuminbe.user.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AccessTokenDto {
    private String access_token;
    private int expires_in;
    private String scope;
    private String token_type;
    private String id_token;
}
