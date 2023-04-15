package com.sparta.bipuminbe.requests.dto;

import com.sparta.bipuminbe.common.enums.RequestType;
import com.sparta.bipuminbe.common.enums.UseType;
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
    @NotBlank
    private String content;
    @NotNull
    private RequestType requestType;
    private UseType useType;
    private List<String> storedImageURLs;
    private List<MultipartFile> multipartFile;
}
