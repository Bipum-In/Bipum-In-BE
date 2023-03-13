package com.sparta.bipuminbe.common.dto;

import com.sparta.bipuminbe.common.entity.ReturnRequest;
import lombok.Getter;

@Getter
public class RetrunRequestDto {

    private String content;

    private String status;

    private RetrunRequestDto(ReturnRequest returnRequset){
        this.content = returnRequset.getContent();
        this.status = returnRequset.getStatus();
    }
}
