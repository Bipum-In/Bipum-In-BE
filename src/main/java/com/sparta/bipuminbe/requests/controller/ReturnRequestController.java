package com.sparta.bipuminbe.requests.controller;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import com.sparta.bipuminbe.common.sse.service.NotificationService;
import com.sparta.bipuminbe.requests.dto.RequestsRequestDto;
import com.sparta.bipuminbe.requests.service.ReturnRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReturnRequestController {

    private final ReturnRequestService returnRequestService;
    private final NotificationService notificationService;

//    @GetMapping("/requests/return/{requestId}")
//    @Operation(summary = "반납 요청 상세 페이지", description = "isAdmin 필드에 따라 버튼 바꿔주면 될 것 같습니다.")
//    public ResponseDto<ReturnRequestResponseDto> getReturnRequest(@PathVariable Long requestId,
//                                  @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return returnRequestService.getReturnRequest(requestId, userDetails.getUser());
//    }

//    @Secured(value = UserRoleEnum.Authority.ADMIN)
//    @PutMapping("/admin/requests/return")
//    @Operation(summary = "반납 요청 승인/거절", description = "acceptResult 승인/거부/폐기 ACCEPT/DECLINE/DISPOSE, 관리자 권한 필요. ")
//    public ResponseDto<String> processingReturnRequest(@PathVariable Long requestId,
//                                                       @RequestParam String acceptResult) {
//
//        // 관리자의 요청 처리 >> 요청자에게 알림 전송.
//        // uri는 해당 알림을 클릭하면 이동할 상세페이지 uri이다.
////        String uri = "/api/requests/return/";
////        notificationService.send(requestId, accep, uri);
//
//        return returnRequestService.processingReturnRequest(requestId, AcceptResult.valueOf(acceptResult));
//    }

    @PostMapping("/requests/return")
    @Operation(summary = "유저의 반납 요청", description = "필요 값 = supplyId, requestType, content, multipartFile(이미지)")
    public ResponseDto<String> supplyRequest(@RequestBody RequestsRequestDto requestsRequestDto,
                                             @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return returnRequestService.returnRequest(requestsRequestDto, userDetails.getUser());
    }
}
