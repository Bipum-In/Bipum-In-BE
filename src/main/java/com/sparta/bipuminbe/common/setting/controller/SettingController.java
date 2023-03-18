package com.sparta.bipuminbe.common.setting.controller;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import com.sparta.bipuminbe.common.setting.dto.SettingResponseDto;
import com.sparta.bipuminbe.common.setting.dto.SwitchResponseDto;
import com.sparta.bipuminbe.common.setting.service.SettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SettingController {
    private final SettingService settingService;

    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @GetMapping("/admin/config")
    @Operation(summary = "부서 목록, 비품 카테고리, 협력업체 전체 조회")
    public ResponseDto<SettingResponseDto> getSettingPage(){

        return settingService.getSettingPage();
    }

    @PutMapping("/config/alarm")
    @Operation(summary = "알림 기능 On/Off", description = "기본값은 알림 On 입니다.")
    public ResponseDto<SwitchResponseDto> switchAlarm(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){

        return settingService.switchAlarm(userDetails.getUser());
    }
}
