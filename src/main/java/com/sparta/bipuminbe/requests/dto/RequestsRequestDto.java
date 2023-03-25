package com.sparta.bipuminbe.requests.dto;

import com.sparta.bipuminbe.common.enums.RequestType;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
public class RequestsRequestDto {
    private Long supplyId;
    private Long categoryId;
    @NotNull
    @NotBlank
    private String content;
    @NotNull
    @NotBlank
    private RequestType requestType;
    private List<String> storedImageURLs;
    private List<MultipartFile> multipartFile;
}
