package com.sparta.bipuminbe.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.enums.TokenState;
import com.sparta.bipuminbe.common.enums.TokenType;
import com.sparta.bipuminbe.common.exception.CustomException;
import com.sparta.bipuminbe.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String accessToken = null;
        String refreshToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie == null) {
                    continue;
                }
                log.info(cookie.getName(), URLDecoder.decode(cookie.getValue(), "UTF-8"));
                if (cookie.getName().equals(JwtUtil.AUTHORIZATION_HEADER)) {
                    accessToken = jwtUtil.resolveToken(cookie);
                } else if (cookie.getName().equals(JwtUtil.REFRESH_HEADER)) {
                    refreshToken = jwtUtil.resolveToken(cookie);
                }
            }
        }

        if (accessToken != null) {
            if (jwtUtil.validateToken(accessToken) == TokenState.VALID) {
                setAuthentication(jwtUtil.getUserInfoFromToken(accessToken).getSubject());
            } else if (jwtUtil.validateToken(accessToken) == TokenState.EXPIRED) {
                // Access Token Cookie 삭제
                ResponseCookie responseCookie = ResponseCookie.from(JwtUtil.AUTHORIZATION_HEADER, null).
                        path("/").
                        httpOnly(true).
                        sameSite("None").
                        secure(true).
                        maxAge(1).
                        build();
                response.addHeader("Set-Cookie", responseCookie.toString());
                jwtExceptionHandler(response, "NEED REISSUE", HttpStatus.SEE_OTHER);
                return;
            }
        } else if (refreshToken != null) {
            if (jwtUtil.validateRefreshToken(refreshToken)) {
                setAuthentication(jwtUtil.getUserInfoFromToken(refreshToken).getSubject());
            }
        }
        filterChain.doFilter(request, response);
    }

    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = jwtUtil.createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    public void jwtExceptionHandler(HttpServletResponse response, String message, HttpStatus httpStatus) {
        response.setStatus(httpStatus.value());
        response.setContentType("application/json;charset=UTF-8");  // 원래는 json만 있었는데 나중에 확인하자.
        try {
            String json = new ObjectMapper().writeValueAsString(new ResponseDto(httpStatus.value(), message));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
