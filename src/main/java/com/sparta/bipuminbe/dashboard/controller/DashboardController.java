package com.sparta.bipuminbe.dashboard.controller;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.enums.LargeCategory;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import com.sparta.bipuminbe.dashboard.dto.AdminMainResponseDto;
import com.sparta.bipuminbe.dashboard.dto.UserMainResponseDto;
import com.sparta.bipuminbe.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DashboardController {
    private final DashboardService dashboardService;


    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @Operation(summary = "관리자용 대쉬보드",
            description = "선택한 카테고리 별 조회 ALL / COMPUTER / DIGITAL / ELECTRONICS / FURNITURE / ETC")
    @GetMapping(value = "/admin/main")
    public ResponseDto<AdminMainResponseDto> getAdminMain(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @RequestParam(defaultValue = "") LargeCategory largeCategory) {
        return dashboardService.getAdminMain(userDetails.getUser(), largeCategory);
    }

    @Operation(summary = "사용자용 대쉬보드")
    @GetMapping("/main")
    public ResponseDto<UserMainResponseDto> getUserMain(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                        @RequestParam(defaultValue = "") LargeCategory largeCategory) {

        return dashboardService.getUserMain(userDetails.getUser(), largeCategory);
    }
}
