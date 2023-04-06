package com.sparta.bipuminbe.supply.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ExcelCoverDto {
    SupplyExcelDto[] excelDtoList;
    List<MultipartFile> multipartFileList;
}
