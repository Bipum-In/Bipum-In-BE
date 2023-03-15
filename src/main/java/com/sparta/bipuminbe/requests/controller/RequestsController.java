package com.sparta.bipuminbe.requests.controller;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import com.sparta.bipuminbe.requests.dto.RequestsResponseDto;
import com.sparta.bipuminbe.requests.dto.SupplyRequestResponseDto;
import com.sparta.bipuminbe.requests.service.RequestsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RequestsController {
    private final RequestsService requestsService;

//    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @GetMapping("/admin/requests")
    @Operation(summary = "요청 현황 페이지", description = "type은 ALL/SUPPLY/REPAIR/RETURN, status는 UNPROCESSED/REPAIRING/PROCESSED")
    public ResponseDto<Page<RequestsResponseDto>> getRequests(@RequestParam(defaultValue = "ALL") String type,
                                                              @RequestParam(defaultValue = "UNPROCESSED") String status,
                                                              @RequestParam(defaultValue = "1") int page) {
        return requestsService.getRequests(type, status, page);
    }
}
