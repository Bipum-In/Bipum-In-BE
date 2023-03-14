package com.sparta.bipuminbe.requests.controller;

import com.sparta.bipuminbe.common.dto.ResponseDto;
import com.sparta.bipuminbe.common.enums.UserRoleEnum;
import com.sparta.bipuminbe.requests.dto.RequestsResponseDto;
import com.sparta.bipuminbe.requests.service.RequestsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RequestsController {
    private final RequestsService requestsService;

//    @Secured(value = UserRoleEnum.Authority.ADMIN)
//    @GetMapping("/admin/requests")
//    public ResponseDto<List<RequestsResponseDto>> getAllRequests() {
//
//    }
}
