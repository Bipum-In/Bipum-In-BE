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
    @Pattern(regexp = "[^ㄱ-ㅎㅏ-ㅣ]{1,30}$", message = "한글 자음/모음이 아닌 30자 이내")
    private String modelName; // 제품명
    @NotBlank
    @Pattern(regexp = "[^ㄱ-ㅎㅏ-ㅣ]{1,50}$", message = "한글 자음/모음이 아닌 50자 이내")
    private String serialNum; // 시리얼 넘버
    private String createdAt; // 등록일자
    private String partners; // 협력업체
    private String empName; // 사용자
    private String deptName; //부서명
    private String image; //이미지
}
