package com.sparta.bipuminbe.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import com.sparta.bipuminbe.user.dto.LoginRequestDto;
import com.sparta.bipuminbe.user.dto.LoginResponseDto;
import com.sparta.bipuminbe.user.dto.UserResponseDto;
import com.sparta.bipuminbe.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @Operation(summary = "로그인 처리", description = "카카오 계정정보 담은 Jwt토큰 발급")
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<LoginResponseDto>> kakaoLogin(@RequestParam String code,
                                                                    @RequestParam String urlType) throws IOException {
        // code: 카카오 서버로부터 받은 인가 코드

        return userService.kakaoLogin(code, urlType);
    }

    //로그인 시, 부서와 유저이름이 없는 경우 반드시 추가입력하게 유도
    @Operation(summary = "사원명, 부서 추가입력", description = "로그인 때 사원명, 부서명이 없는 경우 추가정보 입력")
    @PostMapping("/loginadd")
    public ResponseDto<LoginResponseDto> loginAdd(@Valid @RequestBody LoginRequestDto loginRequestDto,
                                                  @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // 카카오에서 받아오지 못하는 유저이름과 부서 추가입력
        return userService.loginAdd(loginRequestDto, userDetails.getUser());
    }

    @GetMapping("/{deptId}")
    @Operation(summary = "부서별 사원 조회", description = "SelectBox용")
    public ResponseDto<List<UserResponseDto>> getUsersByDept(@PathVariable Long deptId) {
        return userService.getUserByDept(deptId);
    }

    @Operation(summary = "카카오 연결 끊기", description = "앱과 연결된 카카오 계정 연결 끊기")
    @PostMapping("/unlink")
    public ResponseDto<String> unlink(HttpServletRequest request) throws JsonProcessingException {
        String bearerToken = request.getHeader("Authorization");

        return userService.unlink(bearerToken);
    }

    @Operation(summary = "로그인 처리", description = "구글 계정정보 담은 Jwt토큰 발급")
    @PostMapping("/login/google")
    public ResponseEntity<ResponseDto<LoginResponseDto>> googleLogin(@RequestParam String code,
                                                                     @RequestParam String urlType) throws IOException {

        return userService.googleLogin(code, urlType);
    }
}
