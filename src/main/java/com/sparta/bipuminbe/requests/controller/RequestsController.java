package com.sparta.bipuminbe.requests.controller;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.requests.dto.RequestsProcessDto;
import com.sparta.bipuminbe.requests.dto.RequestsResponseDto;
import com.sparta.bipuminbe.requests.service.RequestsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RequestsController {
    private final RequestsService requestsService;

    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @GetMapping("/admin/requests")
    @Operation(summary = "요청 현황 페이지", description = "keyword는 필수 x, type은 ALL/SUPPLY/REPAIR/RETURN/REPORT. " +
            "status는 ALL/UNPROCESSED/PROCESSING/PROCESSED. ALL(전체조회) 일 때는 쿼리 안날려도 되긴함. 관리자 권한 필요.")
    public ResponseDto<Page<RequestsResponseDto>> getRequests(@RequestParam(defaultValue = "") String keyword,
                                                              @RequestParam(defaultValue = "ALL") String type,
                                                              @RequestParam(defaultValue = "ALL") String status,
                                                              @RequestParam(defaultValue = "1") int page,
                                                              @RequestParam (defaultValue = "10") int size) {
        return requestsService.getRequestsPage(keyword, type, status, page, size);
    }

    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PutMapping("/admin/requests")
    @Operation(summary = "요청 승인/거절/폐기", description = "acceptResult 승인/거절/폐기, ACCEPT/DECLINE/DISPOSE. " +
            "비품요청의 승인의 경우 supplyId도 같이 필요. " +
            "거절시 거절 사유(comment) 작성 필수. 관리자 권한 필요.")
    public ResponseDto<String> processingRequest(@RequestBody @Valid RequestsProcessDto requestsProcessDto) {

        // 관리자의 요청 처리 >> 요청자에게 알림 전송.
        // uri는 해당 알림을 클릭하면 이동할 상세페이지 uri이다.
//        String uri = "/api/requests/return/";
//        notificationService.send(requestId, accep, uri);

        return requestsService.processingRequest(requestsProcessDto);
    }
}
