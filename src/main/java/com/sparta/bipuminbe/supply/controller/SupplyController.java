package com.sparta.bipuminbe.supply.controller;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import com.sparta.bipuminbe.supply.dto.*;
import com.sparta.bipuminbe.supply.service.SupplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SupplyController {
    private final SupplyService supplyService;

    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PostMapping("/supply")
    public ResponseDto<String> createSupply(
            @RequestBody @Valid SupplyRequestDto supplyRequestDto) {
        return supplyService.createSupply(supplyRequestDto);
    }

    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PostMapping("/supply/file")
    public ResponseDto<ImageResponseDto> uploadFile(
            @RequestParam MultipartFile file
    ) {
        return supplyService.uploadFile(file);
    }

    @GetMapping("/supply")
    public ResponseDto<List<SupplyResponseDto>> getSupplyList(
            @RequestParam("categoryId") int categoryId
    ) {
        return supplyService.getSupplyList();
    }

//    @GetMapping("/supply/{supplyId}")
//    public ResponseDto<SupplyWholeResponseDto> getSupply(
//            @PathVariable Long supplyId
//    ) {
//        return supplyService.getSupply(supplyId);
//    }

//    @GetMapping("/supply/{userId}")
//    public ResponseDto<List<SupplyUserDto>> getSupplyUserList(
//            @PathVariable Long userId,
//            @AuthenticationPrincipal UserDetailsImpl userDetails
//    ) {
//        return supplyService.getSupplyUserList(userId, userDetails);
//    }

    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PutMapping("/supply")
    public ResponseDto<String> updateSupply(
            @RequestParam("supplyId") Long supplyId,
            @RequestParam("userId") Long userId
            ) {
        return supplyService.updateSupply(supplyId, userId);
    }

    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @DeleteMapping("/supply/{supplyId}")
    public ResponseDto<SupplyResponseDto> deleteSupply(
            @PathVariable Long supplyId
    ) {
        return supplyService.deleteSupply(supplyId);
    }


}
