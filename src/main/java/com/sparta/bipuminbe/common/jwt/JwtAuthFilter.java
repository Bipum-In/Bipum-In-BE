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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtUtil.resolveToken(request, TokenType.ACCESS);
        String refreshToken = jwtUtil.resolveToken(request, TokenType.REFRESH);

        if (accessToken != null) {
            if (jwtUtil.validateToken(accessToken) == TokenState.VALID) {
                setAuthentication(jwtUtil.getUserInfoFromToken(accessToken).getSubject());
            } else if (jwtUtil.validateToken(accessToken) == TokenState.EXPIRED) {
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
