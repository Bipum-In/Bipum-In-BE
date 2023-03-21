package com.sparta.bipuminbe.requests.dto;

import com.sparta.bipuminbe.common.enums.RequestType;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class RequestsRequestDto {
    private Long supplyId;
    private Long categoryId;;
    private String content;
    private RequestType requestType;
    @Nullable
    private MultipartFile multipartFile;
}
