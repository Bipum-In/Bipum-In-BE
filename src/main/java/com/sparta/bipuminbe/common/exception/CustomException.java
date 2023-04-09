package com.sparta.bipuminbe.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    @Getter
    @RequiredArgsConstructor
    public static class ExcelError extends RuntimeException {
        private final String numberMessage;
        private final ErrorCode errorCode;
    }
}