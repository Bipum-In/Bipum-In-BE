package com.sparta.bipuminbe.dashboard.controller;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.enums.LargeCategory;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import com.sparta.bipuminbe.common.sse.dto.NotificationResponseForAdmin;
import com.sparta.bipuminbe.common.sse.dto.NotificationResponseForUser;
import com.sparta.bipuminbe.dashboard.dto.AdminMainResponseDto;
import com.sparta.bipuminbe.dashboard.dto.UserMainResponseDto;
import com.sparta.bipuminbe.dashboard.dto.UserSupplyDto;
import com.sparta.bipuminbe.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DashboardController {
    private final DashboardService dashboardService;


    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @Operation(summary = "관리자용 대쉬보드",
            description = "선택한 카테고리 별 조회 -(전체) / COMPUTER / DIGITAL / ELECTRONICS / FURNITURE / ETC")
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

    @Operation(summary = "Admin용 알림 Page 가져오기")
    @GetMapping(value = "/admin/main/alarm")
    public ResponseDto<Page<NotificationResponseForAdmin>> getAdminAlarm(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                         @RequestParam(defaultValue = "1") int page,
                                                                         @RequestParam(defaultValue = "10") int size){
        return dashboardService.getAdminAlarm(userDetails.getUser(), page, size);
    }

    @Operation(summary = "User용 알림 Page 가져오기")
    @GetMapping(value = "/main/alarm")
    public ResponseDto<Page<NotificationResponseForUser>> getUserAlarm(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                       @RequestParam(defaultValue = "1") int page,
                                                                       @RequestParam(defaultValue = "10") int size){
        return dashboardService.getUserAlarm(userDetails.getUser(), page, size);
    }

    @Operation(summary = "클릭한 알림 읽음 처리")
    @PutMapping(value = "/main/read/{notificationId}")
    public ResponseDto<String> notificationRead(@PathVariable Long notificationId){
        return dashboardService.notificationRead(notificationId);
    }

    @GetMapping("/main/common")
    @Operation(summary = "대쉬보드 공용모드 *신규 Api*",
            description = "선택한 카테고리 별 조회 -(전체) / COMPUTER / DIGITAL / ELECTRONICS / FURNITURE / ETC")
    public ResponseDto<List<UserSupplyDto>> getCommonSupply(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                            @RequestParam(defaultValue = "") LargeCategory largeCategory) {
        return dashboardService.getCommonSupply(userDetails.getUser(), largeCategory);
    }
}
