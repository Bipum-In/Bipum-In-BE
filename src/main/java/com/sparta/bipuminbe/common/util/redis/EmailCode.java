package com.sparta.bipuminbe.common.util.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash
// 비밀번호 찾기 이메일 발송. TTL 활용하기 위해 Redis 사용.
public class EmailCode {
    @Id
    private String pwCode;

    // 아이디
    private String username;

    @TimeToLive
    Long expiration;
    
    @Builder
    public EmailCode(String pwCode, String username, Long expiration) {
        this.pwCode = pwCode;
        this.username = username;
        this.expiration = expiration;
    }}
