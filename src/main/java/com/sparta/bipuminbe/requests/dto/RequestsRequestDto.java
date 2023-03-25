package com.sparta.bipuminbe.requests.dto;

import com.sparta.bipuminbe.common.enums.RequestType;
import com.sun.istack.Nullable;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class RequestsRequestDto {
    private Long supplyId;
    private Long categoryId;
    private String content;
    private String requestType;
    private List<String> storedImageURLs;
    private List<MultipartFile> multipartFile;
}
