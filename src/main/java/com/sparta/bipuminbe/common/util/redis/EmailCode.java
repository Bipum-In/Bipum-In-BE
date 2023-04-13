package com.sparta.bipuminbe.common.util.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash
public class EmailCode {
    @Id
    private String pwCode;
    private String username;
    @TimeToLive
    Long expiration;

    @Builder
    public EmailCode(String pwCode, String username, Long expiration) {
        this.pwCode = pwCode;
        this.username = username;
        this.expiration = expiration;
    }
}
