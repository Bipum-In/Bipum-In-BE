package com.sparta.bipuminbe.requests.controller;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import com.sparta.bipuminbe.requests.dto.ReportProcessRequestDto;
import com.sparta.bipuminbe.requests.dto.ReportRequestResponseDto;
import com.sparta.bipuminbe.requests.dto.RequestsRequestDto;
import com.sparta.bipuminbe.requests.service.ReportRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class ReportRequestController {
    private final ReportRequestService reportRequestService;

    @GetMapping("/requests/report/{reportId}")
    @Operation(summary = "보고서 결재 상세 페이지", description = "isAdmin 필드에 따라 버튼 바꿔주면 될 것 같습니다.")
    public ResponseDto<ReportRequestResponseDto> getReportRequest(@PathVariable Long reportId,
                                      @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportRequestService.getReportRequest(reportId, userDetails.getUser());
    }

    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PutMapping("/admin/requests/report")
    @Operation(summary = "보고서 결재 승인/거부", description = "acceptResult 승인/거절 ACCEPT/DECLINE, " +
            "거절시 거절 사유(comment) 작성 필수")
    public ResponseDto<String> processingReportRequest(@RequestBody ReportProcessRequestDto reportProcessRequestDto) {
        return reportRequestService.processingReportRequest(reportProcessRequestDto);
    }
}
