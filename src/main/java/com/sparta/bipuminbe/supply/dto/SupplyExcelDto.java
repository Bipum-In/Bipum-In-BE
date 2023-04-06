package com.sparta.bipuminbe.supply.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class SupplyExcelDto {
    private String category; // 비품 종류
    private String modelName; // 제품명
    private String serialNum; // 시리얼 넘버
    private LocalDateTime createdAt; // 등록일자
    private String partners; // 협력업체
    private String empName; // 사용자
    private String deptName; //부서명
    private String image; //이미지
    private MultipartFile multipartFile; //이미지

}
