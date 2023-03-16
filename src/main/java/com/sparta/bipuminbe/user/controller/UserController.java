package com.sparta.bipuminbe.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import com.sparta.bipuminbe.user.dto.LoginRequestDto;
import com.sparta.bipuminbe.user.dto.UserResponseDto;
import com.sparta.bipuminbe.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/kakao/callback")
    public ResponseEntity<ResponseDto<Boolean>> kakaoLogin(@RequestParam String code) throws JsonProcessingException {
        // code: 카카오 서버로부터 받은 인가 코드
        return userService.kakaoLogin(code);
    }

    //로그인 시, 부서와 유저이름이 없는 경우 반드시 추가입력하게 유도
    @PostMapping("/loginadd")
    public ResponseDto<String> loginAdd(@Valid @RequestBody LoginRequestDto loginRequestDto, @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){
        // 카카오에서 받아오지 못하는 유저이름과 부서 추가입력
        return  userService.loginAdd(loginRequestDto, userDetails.getUser());
    }

    @GetMapping("/{deptId}")
    @Operation(summary = "부서별 사원 조회", description = "SelectBox용")
    public ResponseDto<List<UserResponseDto>> getUsersByDept(@PathVariable Long deptId) {
        return userService.getUserByDept(deptId);
    }
}
