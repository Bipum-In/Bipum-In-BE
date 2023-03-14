package com.sparta.bipuminbe.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.bipuminbe.common.jwt.JwtUtil;
import com.sparta.bipuminbe.user.service.KakaoService;
import com.sparta.bipuminbe.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final KakaoService kakaoService;

    @PostMapping("/kakao/callback")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        // code: 카카오 서버로부터 받은 인가 코드
        String createToken = kakaoService.kakaoLogin(code, response);

//        // Cookie 생성 및 직접 브라우저에 Set
//        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, createToken.substring(7));
//        // 앞부분 키, 뒤가 밸류
//        cookie.setPath("/");
//        response.addCookie(cookie);

        return "redirect:/api/shop";
    }
}
