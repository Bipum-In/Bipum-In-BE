package com.sparta.bipuminbe.requests.controller;

import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import com.sparta.bipuminbe.requests.dto.RetrunRequestDto;
import com.sparta.bipuminbe.requests.service.ReturnRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class ReturnRequestController {

    private final ReturnRequestService returnRequestService;

    @PostMapping("/return")
    public ResponseEntity<RetrunRequestDto> creatRetrunRequest(@RequestBody RetrunRequestDto retrunRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return returnRequestService.creatRetrunRequest(retrunRequestDto, userDetails.getUser());
    }
}
