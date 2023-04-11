package com.sparta.bipuminbe.common.jwt;

import com.sparta.bipuminbe.common.enums.TokenType;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import com.sparta.bipuminbe.common.security.UserDetailsServiceImpl;
import com.sparta.bipuminbe.common.util.redis.RedisRepository;
import com.sparta.bipuminbe.common.util.redis.RefreshToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final UserDetailsServiceImpl userDetailsService;
    private final RedisRepository redisRepository;

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_HEADER = "Refresh";
    public static final String AUTHORIZATION_KEY = "auth";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final long ACCESS_TOKEN_TIME = 30 * 1000L;  // 1시간
    public static final long REFRESH_TOKEN_TIME = 5 * 60 * 1000L;   // 2주

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // header 토큰을 가져오기
    public String resolveToken(HttpServletRequest request, TokenType tokenType) {
        String bearerToken = tokenType == TokenType.ACCESS ? request.getHeader(AUTHORIZATION_HEADER) : request.getHeader(REFRESH_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 토큰 생성
    public String createToken(String username, UserRoleEnum role, TokenType tokenType) {
        Date date = new Date();
        long time = tokenType == TokenType.ACCESS ? ACCESS_TOKEN_TIME : REFRESH_TOKEN_TIME;

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username)
                        .claim(AUTHORIZATION_KEY, role)
                        .setExpiration(new Date(date.getTime() + time))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    // accessToken 검증
    public Boolean validateToken(String accessToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new CustomException(ErrorCode.TokenSecurityExceptionOrMalformedJwtException);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.TokenNeedReIssue);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(ErrorCode.TokenUnsupportedJwtException);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.TokenIllegalArgumentException);
        }
    }

    // refreshToken 토큰 검증
    public Boolean validateRefreshToken(String refreshToken) {
        // 1차 토큰 검증
        if (!validateToken(refreshToken)) {
            return false;
        }

        // DB에 저장한 토큰 비교
        Optional<RefreshToken> savedRefreshToken = redisRepository.findById(getUserInfoFromToken(refreshToken).getSubject());

        return savedRefreshToken.isPresent() && refreshToken.equals(savedRefreshToken.get().getRefreshToken().substring(7));
    }

//    // 토큰 검증
//    public Boolean tokenValidation(String token) {
//        try {
//            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//            return true;
//        } catch (Exception ex) {
//            log.error(ex.getMessage());
//            return false;
//        }
//    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

}
