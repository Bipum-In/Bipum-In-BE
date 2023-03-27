package com.sparta.bipuminbe.requests.controller;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.enums.RequestStatus;
import com.sparta.bipuminbe.common.enums.RequestType;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import com.sparta.bipuminbe.common.sse.service.NotificationService;
import com.sparta.bipuminbe.requests.dto.RequestsRequestDto;
import com.sparta.bipuminbe.requests.dto.RequestsDetailsResponseDto;
import com.sparta.bipuminbe.requests.dto.RequestsProcessRequestDto;
import com.sparta.bipuminbe.requests.dto.RequestsPageResponseDto;
import com.sparta.bipuminbe.requests.service.RequestsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RequestsController {
    private final RequestsService requestsService;
    private final NotificationService notificationService;

    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @GetMapping("/admin/requests")
    @Operation(summary = "요청 현황 페이지(ADMIN)", description = "keyword는 필수 x.<br> " +
            "type은 **ALL/SUPPLY/REPAIR/RETURN/REPORT**.<br> " +
            "status는 **ALL/UNPROCESSED/PROCESSING/PROCESSED**.<br> " +
            "ALL(전체조회) or 키워드x 일 때는 쿼리 안날려도 되긴함.<br> " +
            "관리자 권한 필요.")
    public ResponseDto<Page<RequestsPageResponseDto>> getRequestsAdminPage(@RequestParam(defaultValue = "") String keyword,
                                                                           @RequestParam(defaultValue = "") RequestType type,
                                                                           @RequestParam(defaultValue = "") RequestStatus status,
                                                                           @RequestParam(defaultValue = "1") int page,
                                                                           @RequestParam(defaultValue = "10") int size,
                                                                           @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return requestsService.getRequestsPage(keyword, type, status, page, size, userDetails.getUser());
    }

    @GetMapping("/requests")
    @Operation(summary = "요청 현황 페이지(USER)", description = "keyword는 필수 x.<br> " +
            "type은 **ALL/SUPPLY/REPAIR/RETURN/REPORT**.<br> " +
            "status는 **ALL/UNPROCESSED/PROCESSING/PROCESSED**.<br> " +
            "ALL(전체조회) or 키워드x 일 때는 쿼리 안날려도 되긴함.")
    public ResponseDto<Page<RequestsPageResponseDto>> getRequestsPage(@RequestParam(defaultValue = "") String keyword,
                                                                      @RequestParam(defaultValue = "") RequestType type,
                                                                      @RequestParam(defaultValue = "") RequestStatus status,
                                                                      @RequestParam(defaultValue = "1") int page,
                                                                      @RequestParam(defaultValue = "10") int size,
                                                                      @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return requestsService.getRequestsPage(keyword, type, status, page, size, userDetails.getUser());
    }

    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PutMapping("/admin/requests/{requestId}")
    @Operation(summary = "요청 승인/거절/폐기", description = "acceptResult 승인/거절/폐기, ACCEPT/DECLINE/DISPOSE. " +
            "비품요청의 승인의 경우 supplyId도 같이 필요. " +
            "거절시 거절 사유(comment) 작성 필수. 관리자 권한 필요.")
    public ResponseDto<String> processingRequests(@PathVariable Long requestId,
                                                  @RequestBody @Valid RequestsProcessRequestDto requestsProcessRequestDto) throws Exception {

        ResponseDto<String> result = requestsService.processingRequests(requestId, requestsProcessRequestDto);
//         관리자의 요청 처리 >> 요청자에게 알림 전송.
//         uri는 해당 알림을 클릭하면 이동할 상세페이지 uri이다.
        String uri = "/api/requests/";
        notificationService.send(requestId,
                requestsProcessRequestDto.getAcceptResult(), uri);

        return result;
    }

    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @GetMapping("/admin/requests/{requestId}")
    @Operation(summary = "요청서 상세 페이지(ADMIN)",
            description = "isAdmin/requestType/requestStatus 필드에 따라 버튼 바꿔주시면 될 것 같습니다.")
    public ResponseDto<RequestsDetailsResponseDto> getRequestsDetails(@PathVariable Long requestId,
                                                                      @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return requestsService.getRequestsDetails(requestId, userDetails.getUser(), UserRoleEnum.ADMIN);
    }

    @GetMapping("/requests/{requestId}")
    @Operation(summary = "요청서 상세 페이지(USER)",
            description = "isAdmin/requestType/requestStatus 필드에 따라 버튼 바꿔주시면 될 것 같습니다.")
    public ResponseDto<RequestsDetailsResponseDto> getRequestsAdminDetails(@PathVariable Long requestId,
                                                                           @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return requestsService.getRequestsDetails(requestId, userDetails.getUser(), UserRoleEnum.USER);
    }

    @PostMapping(value = "/requests", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "유저 요청 페이지", description = "**비품 요청**일 경우, 필요값 = categoryId, requestType, content<br>" +
            "**반납/수리/보고서 일 경우**, 필요값 = supplyId, requestType, content, multipartFile(이미지)<br>" +
            "requestType = SUPPLY / REPAIR / RETURN / REPORT")
    public ResponseDto<String> createRequests(@ModelAttribute @Valid RequestsRequestDto requestsRequestDto,
                                              @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception {
        return requestsService.createRequests(requestsRequestDto, userDetails.getUser());
    }

    @PutMapping(value = "/requests/{requestId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "유저 요청 수정 페이지", description = "**비품 요청**일 경우, 필요값 = categoryId, requestType, content<br>" +
            "**반납/수리/보고서 일 경우**, 필요값 = supplyId, requestType, content, multipartFile(이미지)<br>" +
            "requestType = SUPPLY / REPAIR / RETURN / REPORT<br>" +
            "**처리 전의 요청**에 한해서만 수정 가능")
    public ResponseDto<String> updateRequests(@PathVariable Long requestId,
                                              @ModelAttribute @Valid RequestsRequestDto requestsRequestDto,
                                              @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return requestsService.updateRequests(requestId, requestsRequestDto, userDetails.getUser());
    }

    @DeleteMapping("/requests/{requestId}")
    @Operation(summary = "유저 요청 삭제 페이지", description = "**처리 전의 요청**에 한해서만 삭제 가능")
    public ResponseDto<String> deleteRequests(@PathVariable Long requestId,
                                              @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return requestsService.deleteRequests(requestId, userDetails.getUser());
    }
}
