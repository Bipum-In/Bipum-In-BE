package com.sparta.bipuminbe.requests.dto;

import com.sparta.bipuminbe.common.enums.RequestType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class RequestsRequestDto {
    private Long supplyId;
    private Long categoryId;
    private String content;
    private RequestType requestType;
    @Nullable
    private List<MultipartFile> multipartFile;
}
