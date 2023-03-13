package com.sparta.bipuminbe.common.dto;

import com.sparta.bipuminbe.common.entity.ReturnRequset;
import lombok.Getter;

@Getter
public class RetrunRequestDto {

    private String content;

    private String status;

    private RetrunRequestDto(ReturnRequset returnRequset){
        this.content = returnRequset.getContent();
        this.status = returnRequset.getStatus();
    }
}
