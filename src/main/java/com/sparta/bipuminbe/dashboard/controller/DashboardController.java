package com.sparta.bipuminbe.dashboard.controller;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.dashboard.dto.AdminMainResponseDto;
import com.sparta.bipuminbe.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DashboardController {
    private final DashboardService dashboardService;

    @Operation(summary = "관리자용 대쉬보드")
    @GetMapping("/admin/main")
    public ResponseDto<AdminMainResponseDto> getAdminMain(){

        return dashboardService.getAdminMain();
    }

}
