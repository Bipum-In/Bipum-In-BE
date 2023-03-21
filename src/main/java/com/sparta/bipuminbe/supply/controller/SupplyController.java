package com.sparta.bipuminbe.supply.controller;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import com.sparta.bipuminbe.supply.dto.*;
import com.sparta.bipuminbe.supply.service.SupplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SupplyController {
    private final SupplyService supplyService;


    //비품 등록
    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PostMapping("/supply")
    @Operation(summary = "비품 등록", description = "카테고리(null 불가), 모델 이름(null 불가), 시리얼 번호(null 불가), 반납 날짜(null 가능), 협력업체(null 가능), 유저 아이디(null 불가), 관리자 권한 필요.")
    public ResponseDto<String> createSupply(
            @RequestBody @Valid SupplyRequestDto supplyRequestDto) {
        return supplyService.createSupply(supplyRequestDto);
    }


    //비품 조회
    @GetMapping("/supply")
    @Operation(summary = "비품 조회", description = "SelectBox용(카테고리), 관리자 권한 필요. status ALL/USING/STOCK/REPAIRING")
    public ResponseDto<Page<SupplyResponseDto>> getSupplyList(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String categoryId,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return supplyService.getSupplyList(keyword, categoryId, status, page, size);
    }

    //비품 상세
    @GetMapping("/supply/{supplyId}")
    @Operation(summary = "비품 상세", description = "관리자 권한 필요.")
    public ResponseDto<SupplyWholeResponseDto> getSupply(
            @PathVariable Long supplyId
    ) {
        return supplyService.getSupply(supplyId);
    }

    //유저 할당
    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PutMapping("/supply")
    @Operation(summary = "유저 할당", description = "SelectBox용(카테고리), 관리자 권한 필요.")
    public ResponseDto<String> updateSupply(
            @RequestParam("supplyId") Long supplyId,
            @RequestParam("userId") Long userId
            ) {
        return supplyService.updateSupply(supplyId, userId);
    }

    //비품 폐기
    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @DeleteMapping("/supply/{supplyId}")
    @Operation(summary = "비품 폐기", description = "관리자 권한 필요.")
    public ResponseDto<String> deleteSupply(
            @PathVariable Long supplyId
    ) {
        return supplyService.deleteSupply(supplyId);
    }

    //자신의 비품 목록(selectbox용)
    @GetMapping("/supply/mysupply")
    @Operation(summary = "자신의 비품 목록 조회", description = "SelectBox용")
    public ResponseDto<List<SupplyUserDto>> getSupplyUser(@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return supplyService.getSupplyUser(userDetails.getUser());
    }

}
