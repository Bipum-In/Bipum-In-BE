package com.sparta.bipuminbe.supply.controller;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.supply.dto.*;
import com.sparta.bipuminbe.supply.service.SupplyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseDto<String> createSupply(
            @RequestBody @Valid SupplyRequestDto supplyRequestDto) {
        return supplyService.createSupply(supplyRequestDto);
    }


    //비품 조회
    @GetMapping("/supply")
    public ResponseDto<List<SupplyResponseDto>> getSupplyList(
            @RequestParam("categoryId") int categoryId
    ) {
        return supplyService.getSupplyList(categoryId);
    }

    //비품 상세
    @GetMapping("/supply/{supplyId}")
    public ResponseDto<SupplyWholeResponseDto> getSupply(
            @PathVariable Long supplyId
    ) {
        return supplyService.getSupply(supplyId);
    }

    //유저 할당
    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PutMapping("/supply")
    public ResponseDto<String> updateSupply(
            @RequestParam("supplyId") Long supplyId,
            @RequestParam("userId") Long userId
            ) {
        return supplyService.updateSupply(supplyId, userId);
    }

    //비품 폐기
    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @DeleteMapping("/supply/{supplyId}")
    @Operation(summary = "비품 폐기")
    public ResponseDto<String> deleteSupply(
            @PathVariable Long supplyId
    ) {
        return supplyService.deleteSupply(supplyId);
    }

    //자신의 비품 목록(selectbox용)
    @GetMapping("/supply/{userId}")
    @Operation(summary = "자신의 비품 목록 조회", description = "SelectBox용")
    public ResponseDto<List<SupplyUserDto>> getSupplyUser(@PathVariable Long userId) {
        return supplyService.getSupplyUser(userId);
    }

}
