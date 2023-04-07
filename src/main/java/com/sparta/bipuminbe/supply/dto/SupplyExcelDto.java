package com.sparta.bipuminbe.supply.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class SupplyExcelDto {
    @NotNull
    @NotBlank
    private String category; // 비품 종류
    @NotNull
    @NotBlank
    private String modelName; // 제품명
    @NotNull
    @NotBlank
    private String serialNum; // 시리얼 넘버
    private String createdAt; // 등록일자
    private String partners; // 협력업체
    private String empName; // 사용자
    private String deptName; //부서명
    private String image; //이미지
}
