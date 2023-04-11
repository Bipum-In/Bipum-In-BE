package com.sparta.bipuminbe.common.util.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash
public class RefreshToken {
    @Id
    private String username;

    private String refreshToken;

    private String ip;

    @TimeToLive
    private Long expiration;

    @Builder
    public RefreshToken(String username, String refreshToken, String ip, Long expiration) {
        this.username = username;
        this.refreshToken = refreshToken;
        this.ip = ip;
        this.expiration = expiration;
    }

    public RefreshToken updateToken(String refreshToken, Long expiration) {
        this.refreshToken = refreshToken;
        this.expiration = expiration;
        return this;
    }

}
