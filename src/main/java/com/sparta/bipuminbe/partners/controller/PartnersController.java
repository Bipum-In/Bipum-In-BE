package com.sparta.bipuminbe.partners.controller;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.partners.dto.PartnersDto;
import com.sparta.bipuminbe.partners.service.PartnersService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PartnersController {
    private final PartnersService partnersService;

    @GetMapping("/partners")
    @Operation(summary = "협력 업체 목록", description = "SelectBox용")
    public ResponseDto<List<PartnersDto>> getPartnersList() {
        return partnersService.getPartnersList();
    }

//    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PostMapping("/partners")
    @Operation(summary = "협력 업체 등록", description = "이름(NotNull), 번호, 주소 입력")
    public ResponseDto<String> createPartners(@RequestBody @Valid PartnersDto partnersDto) {
        return partnersService.createPartners(partnersDto);
    }

//    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @PutMapping("/partners/{partnersId}")
    @Operation(summary = "협력 업체 정보 수정", description = "이름(NotNull), 번호, 주소 입력")
    public ResponseDto<String> updatePartners(@PathVariable Long partnersId,
                                              @RequestBody @Valid PartnersDto partnersDto) {
        return partnersService.updatePartners(partnersId, partnersDto);
    }

//    @Secured(value = UserRoleEnum.Authority.ADMIN)
    @DeleteMapping("/partners/{partnersId}")
    @Operation(summary = "협력 업체 삭제")
    public ResponseDto<String> deletePartners(@PathVariable Long partnersId) {
        return partnersService.deletePartners(partnersId);
    }
}
