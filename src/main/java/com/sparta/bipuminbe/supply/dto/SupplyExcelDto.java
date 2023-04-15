package com.sparta.bipuminbe.supply.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SupplyExcelDto {
    @NotBlank
    private String category; // 비품 종류
    @NotBlank
    @Pattern(regexp = "[^ㄱ-ㅎㅏ-ㅣ]*${4,30}")
    private String modelName; // 제품명
    @NotBlank
    @Pattern(regexp = "[^ㄱ-ㅎㅏ-ㅣ]*${4,50}")
    private String serialNum; // 시리얼 넘버
    private String createdAt; // 등록일자
    private String partners; // 협력업체
    private String empName; // 사용자
    private String deptName; //부서명
    private String image; //이미지
}
