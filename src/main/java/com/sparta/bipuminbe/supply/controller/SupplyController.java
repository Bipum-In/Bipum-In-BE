package com.sparta.bipuminbe.supply.controller;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.enums.SupplyStatusEnum;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.common.security.UserDetailsImpl;
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


    //비품 등록
    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PostMapping(value = "/supply", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "비품 등록", description = "카테고리(null 불가), 모델 이름(null 불가), 시리얼 번호(null 불가), 반납 날짜(null 가능), 협력업체(null 가능), 유저 아이디(null 불가), 관리자 권한 필요.")
    public ResponseDto<String> createSupply(
            @ModelAttribute @Valid SupplyRequestDto supplyRequestDto) throws IOException {
        return supplyService.createSupply(supplyRequestDto);
    }


    //비품 조회
    @GetMapping("/admin/supply")
    @Operation(summary = "비품 조회 페이지(ADMIN)", description = "SelectBox용(카테고리), 관리자 권한 필요. status ALL/USING/STOCK/REPAIRING")
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
    @Operation(summary = "비품 상세(ADMIN)", description = "관리자 권한 필요." +
            "history의 경우 선택적으로 데이터 챙겨주시면 감사합니다.")
    public ResponseDto<SupplyWholeResponseDto> getAdminSupply(
            @PathVariable Long supplyId,
            @RequestParam(defaultValue = "6") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return supplyService.getSupply(supplyId, size, userDetails.getUser(), UserRoleEnum.ADMIN);
    }

    //비품 상세(USER)
    @GetMapping("/supply/{supplyId}")
    @Operation(summary = "비품 상세(USER)", description = "history의 경우 선택적으로 데이터 챙겨주시면 감사합니다.")
    public ResponseDto<SupplyWholeResponseDto> getSupply(
            @PathVariable Long supplyId,
            @RequestParam(defaultValue = "6") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return supplyService.getSupply(supplyId, size, userDetails.getUser(), UserRoleEnum.USER);
    }

    //비품 수정
    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PutMapping(value = "/supply/{supplyId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "비품 수정", description = "관리자 권한 필요.")
    public ResponseDto<String> updateSupplies(
            @PathVariable Long supplyId,
            @ModelAttribute @Valid SupplyRequestDto supplyRequestDto
    ) throws IOException {
        return supplyService.updateSupplies(supplyId, supplyRequestDto);
    }

    //비품 폐기
    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @DeleteMapping("/supply/{supplyId}")
    @Operation(summary = "비품 폐기", description = "관리자 권한 필요.")
    public ResponseDto<String> deleteSupply(
            @PathVariable Long supplyId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return supplyService.deleteSupply(supplyId, userDetails.getUser());
    }

    //자신의 비품 목록(selectbox용)
    @GetMapping("/supply/mysupply")
    @Operation(summary = "자신의 비품 목록 조회", description = "SelectBox용")
    public ResponseDto<List<SupplyUserDto>> getSupplyUser(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return supplyService.getSupplyUser(userDetails.getUser());
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
