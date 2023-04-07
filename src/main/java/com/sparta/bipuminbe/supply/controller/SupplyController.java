package com.sparta.bipuminbe.supply.controller;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.entity.Requests;
import com.sparta.bipuminbe.common.enums.AcceptResult;
import com.sparta.bipuminbe.common.enums.SupplyStatusEnum;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import com.sparta.bipuminbe.common.sse.service.NotificationService;
import com.sparta.bipuminbe.supply.dto.*;
import com.sparta.bipuminbe.supply.service.SupplyService;
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
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SupplyController {
    private final SupplyService supplyService;
    private final NotificationService notificationService;

    //비품 등록
    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PostMapping(value = "/supply", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "비품 등록 *수정사항 있습니다.*", description = "카테고리(null 불가), 모델 이름(null 불가), 시리얼 번호(null 불가). 관리자 권한 필요. <br>" +
            "개인 : UseType = PERSONAL, + userId <br>" +
            "공용 : UseType = COMMON, + deptId")
    public ResponseDto<String> createSupply(
            @ModelAttribute @Valid SupplyRequestDto supplyRequestDto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {

        Requests requests = supplyService.createSupply(supplyRequestDto, userDetails.getUser());

        if (supplyRequestDto.getUserId() != null) {
            notificationService.sendForUser(userDetails.getUser(), requests.getRequestId(), AcceptResult.ASSIGN);
        }
        return ResponseDto.success("비품 등록 완료");
    }

    //비품 복수 등록
    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PostMapping(value = "/supply/excel", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "비품 복수 등록", description = "카테고리(null 불가), 모델 이름(null 불가), 시리얼 번호(null 불가), 반납 날짜(null 가능), 협력업체(null 가능), 유저 아이디(null 불가), 관리자 권한 필요.")
    public ResponseDto<String> createSupplies(
            @ModelAttribute ExcelCoverDto excelCoverDto) throws IOException {

        return supplyService.createSupplies(excelCoverDto);
    }

    //비품 조회
    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @GetMapping("/admin/supply")
    @Operation(summary = "비품 조회 페이지(ADMIN) *수정사항 있습니다.*", description = "SelectBox용(카테고리), 관리자 권한 필요. <br>" +
            "status ALL/USING/STOCK/REPAIRING. <br>" +
            "수정 사항 : userName -> empName")
    public ResponseDto<Page<SupplyResponseDto>> getSupplyList(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") Long categoryId,
            @RequestParam(defaultValue = "") SupplyStatusEnum status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return supplyService.getSupplyList(keyword, categoryId, status, page, size);
    }

    //비품 상세(ADMIN)
    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @GetMapping("/admin/supply/{supplyId}")
    @Operation(summary = "비품 상세(ADMIN) *수정사항 있습니다.*", description = "관리자 권한 필요. history의 경우 선택적으로 데이터 챙겨주시면 감사합니다. <br>" +
            "수정사항 : Response에 useType(개인/공용) 추가.")
    public ResponseDto<SupplyWholeResponseDto> getAdminSupply(
            @PathVariable Long supplyId,
            @RequestParam(defaultValue = "6") int size,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return supplyService.getSupply(supplyId, size, userDetails.getUser(), UserRoleEnum.ADMIN);
    }

    //비품 상세(USER)
    @GetMapping("/supply/{supplyId}")
    @Operation(summary = "비품 상세(USER) *수정사항 있습니다.*", description = "history의 경우 선택적으로 데이터 챙겨주시면 감사합니다. <br>" +
            "수정사항 : Response에 useType(개인/공용) 추가.")
    public ResponseDto<SupplyWholeResponseDto> getSupply(
            @PathVariable Long supplyId,
            @RequestParam(defaultValue = "6") int size,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return supplyService.getSupply(supplyId, size, userDetails.getUser(), UserRoleEnum.USER);
    }

    //비품 수정
    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PutMapping(value = "/supply/{supplyId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "비품 수정 *수정사항 있습니다.*", description = "관리자 권한 필요.<br>" +
            "수정 시 변경되는 곳은 partnersId, image, useType, userId, deptId 입니다. 나머지는 기존 데이터 입력해주시면 됩니다.<br>" +
            "개인 : UseType = PERSONAL, + userId <br>" +
            "공용 : UseType = COMMON, + deptId <br>" +
            "재고 : UseType = Null(비움)")
    public ResponseDto<String> updateSupplies(
            @PathVariable Long supplyId,
            @ModelAttribute @Valid SupplyRequestDto supplyRequestDto,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {

        List<Requests> requests = supplyService.updateSupplies(supplyId, supplyRequestDto, userDetails.getUser());

        for (Requests request : requests) {
            if (supplyRequestDto.getUserId() != null) {
                notificationService.sendForUser(userDetails.getUser(), request.getRequestId(), AcceptResult.ASSIGN);
            }
        }
        return ResponseDto.success("비품 수정 성공");
    }

    //비품 폐기
    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @DeleteMapping("/supply/{supplyId}")
    @Operation(summary = "비품 폐기", description = "관리자 권한 필요.")
    public ResponseDto<String> deleteSupply(
            @PathVariable Long supplyId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Requests request = supplyService.deleteSupply(supplyId, userDetails.getUser());

        notificationService.sendForUser(userDetails.getUser(), request.getRequestId(), AcceptResult.ASSIGN);

        return ResponseDto.success("비품 삭제 성공");
    }

    // 자신의 비품 목록(selectbox용)
    @GetMapping("/supply/mysupply/{categoryId}")
    @Operation(summary = "자신의 비품 목록 조회", description = "SelectBox용")
    public ResponseDto<List<SupplyUserDto>> getSupplyUser(@PathVariable Long categoryId,
                                                          @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return supplyService.getSupplyUser(categoryId, userDetails.getUser());
    }

    // 자신의 부서 공용 비품 목록(selectbox용)
    @GetMapping("/supply/common/mysupply/{categoryId}")
    @Operation(summary = "자신의 부서의 공용 비품 목록 조회", description = "SelectBox용")
    public ResponseDto<List<SupplyUserDto>> getMyCommonSupply(@PathVariable Long categoryId,
                                                              @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return supplyService.getMyCommonSupply(categoryId, userDetails.getUser());
    }

    // 비품 요청 상세 페이지 SelectBox.
    @GetMapping("/supply/stock/{categoryId}")
    @Operation(summary = "재고 비품 조회(비품 요청 페이지)", description = "비품 요청 상세 페이지. SelectBox용.")
    public ResponseDto<List<StockSupplyResponseDto>> getStockSupply(@PathVariable Long categoryId) {
        return supplyService.getStockSupply(categoryId);
    }

    // 비품 이미지 search
    @GetMapping("/supply/search")
    @Operation(summary = "naver Api를 통한 이미지 서치")
    public ResponseDto<ImageResponseDto> getImageByNaver(@RequestParam String modelName) {
        return supplyService.getImageByNaver(modelName);
    }

    // 비품 리스트 UserPage
    @GetMapping("/supply")
    @Operation(summary = "재고 현황 페이지(USER)", description = "데이터 골라서 집어가주실 수 있을까요 ㅋㅋㅋㅋ <br>" +
            "supplyId / image / modelName / createdAt 가져가시면 될 것 같습니다.")
    public ResponseDto<Page<SupplyResponseDto>> getStockList(@RequestParam(defaultValue = "") String keyword,
                                                             @RequestParam(defaultValue = "") Long categoryId,
                                                             @RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        return supplyService.getStockList(keyword, categoryId, page, size);
    }

    // 유저 사용 내역(비품 상세 페이지 무한 스크롤)
    @GetMapping("/supply/history/user/{supplyId}")
    @Operation(summary = "유저 사용 내역(비품 상세 페이지 무한 스크롤)")
    public ResponseDto<Page<SupplyHistoryResponseDto>> getUserHistory(@PathVariable Long supplyId,
                                                                      @RequestParam(defaultValue = "1") int page,
                                                                      @RequestParam(defaultValue = "6") int size) {
        return supplyService.getUserHistory(supplyId, page, size);
    }

    // 수리 내역(비품 상세 페이지 무한 스크롤)
    @GetMapping("/supply/history/repair/{supplyId}")
    @Operation(summary = "수리 내역(비품 상세 페이지 무한 스크롤)")
    public ResponseDto<Page<SupplyHistoryResponseDto>> getRepairHistory(@PathVariable Long supplyId,
                                                                        @RequestParam(defaultValue = "1") int page,
                                                                        @RequestParam(defaultValue = "6") int size) {
        return supplyService.getRepairHistory(supplyId, page, size);
    }
}
