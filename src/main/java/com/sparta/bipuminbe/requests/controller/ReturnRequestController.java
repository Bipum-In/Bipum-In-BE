package com.sparta.bipuminbe.requests.controller;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import com.sparta.bipuminbe.requests.dto.RepairRequestResponseDto;
import com.sparta.bipuminbe.requests.dto.ReturnRequestResponseDto;
import com.sparta.bipuminbe.requests.service.ReturnRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReturnRequestController {

    private final ReturnRequestService returnRequestService;

    @GetMapping("/requests/return/{requestId}")
    @Operation(summary = "반납 요청 상세 페이지", description = "isAdmin 필드에 따라 버튼 바꿔주면 될 것 같습니다.")
    public ResponseDto<ReturnRequestResponseDto> getReturnRequest(@PathVariable Long requestId,
                                  @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return returnRequestService.getReturnRequest(requestId, userDetails.getUser());
    }

    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PutMapping("/requests/return/{requestId}")
    @Operation(summary = "반납 요청 승인/거절", description = "isAccepted는 승인/거부")
    public ResponseDto<String> processingReturnRequest(@PathVariable Long requestId,
                                                       @RequestParam Boolean isAccepted) {
        return returnRequestService.processingReturnRequest(requestId, isAccepted);
    }
}
