package com.sparta.bipuminbe.requests.controller;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import com.sparta.bipuminbe.common.sse.service.NotificationService;
import com.sparta.bipuminbe.requests.dto.RequestsRequestDto;
import com.sparta.bipuminbe.requests.service.RepairRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RepairRequestController {

    private final RepairRequestService repairRequestService;
    private final NotificationService notificationService;

//    @GetMapping("/requests/repair/{requestId}")
//    @Operation(summary = "수리 요청 상세 페이지", description = "isAdmin/requestStatus 필드에 따라 버튼 바꿔주면 될 것 같습니다. " +
//            "생각해 봤는데, 처리 중 상태에서는 거절 버튼 없애 줄 수 있나요? " +
//            "수리완료 버튼 하나만 있으면 될 것 같습니다.")
//    public ResponseDto<RepairRequestResponseDto> getRepairRequest(@PathVariable Long requestId,
//                                  @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return repairRequestService.getRepairRequest(requestId, userDetails.getUser());
//    }

//    @Secured(value = UserRoleEnum.Authority.ADMIN)
//    @PutMapping("/admin/requests/repair")
//    @Operation(summary = "수리 요청 승인/거절", description = "acceptResult 승인/거절 ACCEPT/DECLINE. " +
//            "거절시 거절 사유(comment) 작성 필수. 관리자 권한 필요. " +
//            "처리 전 요청 -> 처리 중 / 처리 중 -> 처리 완료.")
//    public ResponseDto<String> processingRepairRequest(@RequestBody @Valid RepairProcessRequestDto repairProcessRequestDto) {
//
//        // 관리자의 요청 처리 >> 요청자에게 알림 전송.
//        // uri는 해당 알림을 클릭하면 이동할 상세페이지 uri이다.
////        String uri = "/api/requests/repair/";
////        notificationService.send(requestId, isAccepted, uri);
//
//        return repairRequestService.processingRepairRequest(repairProcessRequestDto);
//    }
}
