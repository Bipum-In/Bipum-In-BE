package com.sparta.bipuminbe.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    DuplicatedCategory(HttpStatus.BAD_REQUEST, "이미 존재하는 카테고리 입니다."),
    NotFoundCategory(HttpStatus.NOT_FOUND, "카레고리가 존재하지 않습니다."),
    DuplicatedDepartment(HttpStatus.BAD_REQUEST, "이미 존재하는 부서입니다."),
    NotFoundDepartment(HttpStatus.NOT_FOUND, "해당 부서가 존재하지 않습니다."),
    DuplicatedPartners(HttpStatus.BAD_REQUEST, "이미 등록된 협력업체 입니다."),
    NotFoundPartners(HttpStatus.NOT_FOUND, "해당 업체가 존재하지 않습니다."),
    NotFoundUsers(HttpStatus.NOT_FOUND, "해당 사용자가 존재하지 않습니다."),
    NotFoundRequest(HttpStatus.NOT_FOUND, "존재하지 않는 요청입니다."),
    NotFoundImages(HttpStatus.NOT_FOUND, "해당 요청의 이미지를 찾을 수 없습니다."),
    NotFoundFileInS3(HttpStatus.NOT_FOUND, "해당 파일을 S3에서 찾을 수 없습니다."),
    NotAllowedMethod(HttpStatus.METHOD_NOT_ALLOWED, "잘못된 요청입니다."),
    NoPermission(HttpStatus.BAD_REQUEST, "해당 요청에 대한 권한이 없습니다."),
    NotFoundSupply(HttpStatus.NOT_FOUND, "해당 비품이 존재하지 않습니다."),
    NullComment(HttpStatus.BAD_REQUEST, "거절 사유를 작성해 주세요."),
    NotStockSupply(HttpStatus.BAD_REQUEST, "사용 중인 비품입니다."),
    isProcessingRequest(HttpStatus.BAD_REQUEST, "처리 중인 요청이 있습니다."),
    NullImageList(HttpStatus.BAD_REQUEST, "저장된 이미지가 없습니다."),
    NotUnProcessedRequest(HttpStatus.BAD_REQUEST, "처리 전 요청만 수정/삭제가 가능합니다."),
    InValidRequest(HttpStatus.BAD_REQUEST, "해당 키워드의 이미지를 검색할 수 없습니다."),
    JsonConvertError(HttpStatus.BAD_REQUEST, "Json 형식으로 변환에 실패했습니다."),
    UnAuthorized(HttpStatus.UNAUTHORIZED, "로그인을 해주세요."),
    NotFoundUser(HttpStatus.BAD_REQUEST, "아이디가 존재하지 않습니다."),
    NotMatchPassword(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    NotAllowSamePassword(HttpStatus.BAD_REQUEST, "이전과 동일한 비밀번호입니다."),
    NotMatchAdminPassword(HttpStatus.BAD_REQUEST, "관리자 암호가 일치하지 않습니다."),
    DuplicateUsername(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디 입니다."),
    DuplicatedNickname(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임 입니다."),
    NotMatchUser(HttpStatus.BAD_REQUEST, "작성자가 일치하지 않습니다."),
    InValidException(HttpStatus.BAD_REQUEST, "값이 잘못되었습니다."),
    TokenSecurityExceptionOrMalformedJwtException(HttpStatus.BAD_REQUEST, "Invalid JWT signature, 유효하지 않는 JWT 서명 입니다."),
    TokenExpiredJwtException(HttpStatus.BAD_REQUEST, "Expired JWT token, 만료된 JWT token 입니다."),
    TokenUnsupportedJwtException(HttpStatus.BAD_REQUEST, "Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다."),
    TokenIllegalArgumentException(HttpStatus.BAD_REQUEST, "JWT claims is empty, 잘못된 JWT 토큰 입니다."),
    RefreshTokenValidException(HttpStatus.BAD_REQUEST, "refreshToken이 유효하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}