package com.sparta.bipuminbe.requests.controller;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import com.sparta.bipuminbe.requests.dto.RequestsRequestDto;
import com.sparta.bipuminbe.requests.dto.RequestsResponseDto;
import com.sparta.bipuminbe.requests.service.RequestsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RequestsController {
    private final RequestsService requestsService;

    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @GetMapping("/admin/requests")
    @Operation(summary = "요청 현황 페이지", description = "keyword는 필수 x, type은 ALL/SUPPLY/REPAIR/RETURN/REPORT, " +
            "status는 ALL/UNPROCESSED/PROCESSING/PROCESSED, ALL(전체조회) 일 때는 쿼리 안날려도 되긴함. 관리자 권한 필요.")
    public ResponseDto<Page<RequestsResponseDto>> getRequests(@RequestParam(defaultValue = "") String keyword,
                                                              @RequestParam(defaultValue = "ALL") String type,
                                                              @RequestParam(defaultValue = "ALL") String status,
                                                              @RequestParam(defaultValue = "1") int page,
                                                              @RequestParam (defaultValue = "10") int size) {
        return requestsService.getRequests(keyword, type, status, page, size);
    }

    @PostMapping(value = "/requests", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "유저 요청 페이지", description = "**비품 요청**일 경우, 필요값 = categoryId, requestType, content<br>" +
            "**반납/수리/보고서 일 경우**, 필요값 = supplyId, requestType, content, multipartFile(이미지)<br>" +
            "requestType = SUPPLY / REPAIR / RETURN / REPORT")
    public ResponseDto<String> createRequests(@ModelAttribute RequestsRequestDto requestsRequestDto,
                                             @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return requestsService.createRequests(requestsRequestDto, userDetails.getUser());
    }
}
