package com.sparta.bipuminbe.requests.controller;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import com.sparta.bipuminbe.requests.dto.RepairRequestResponseDto;
import com.sparta.bipuminbe.requests.service.RepairRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RepairRequestController {

    private final RepairRequestService repairRequestService;

    @GetMapping("/requests/repair/{requestId}")
    @Operation(summary = "수리 요청 상세 페이지", description = "isAdmin 필드에 따라 버튼 바꿔주면 될 것 같습니다.")
    public ResponseDto<RepairRequestResponseDto> getRepairRequest(@PathVariable Long requestId,
                                  @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return repairRequestService.getRepairRequest(requestId, userDetails.getUser());
    }
}
