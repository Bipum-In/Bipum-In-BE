package com.sparta.bipuminbe.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.user.dto.LoginRequestDto;
import com.sparta.bipuminbe.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService kakaoService;

    @GetMapping("/kakao/callback")
    public ResponseEntity<ResponseDto<Boolean>> kakaoLogin(@RequestParam String code) throws JsonProcessingException {
        // code: 카카오 서버로부터 받은 인가 코드

        return kakaoService.kakaoLogin(code);
    }

//    @PostMapping("/loginadd")
//    public String loginAdd(@Valid @RequestBody LoginRequestDto loginRequestDto){
//        kakaoService.loginAdd(loginRequestDto);
//        return "1";
//    }
}
