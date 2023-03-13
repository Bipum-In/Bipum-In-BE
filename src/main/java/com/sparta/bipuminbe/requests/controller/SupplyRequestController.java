package com.sparta.bipuminbe.requests.controller;

import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import com.sparta.bipuminbe.requests.dto.SupplyRequestDto;
import com.sparta.bipuminbe.requests.service.SupplyRequestService;
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
public class SupplyRequestController {

    private final SupplyRequestService supplyRequestService;

    @PostMapping("/supply")
    public ResponseEntity<SupplyRequestDto> creatSupplyRequest(@RequestBody SupplyRequestDto supplyRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return supplyRequestService.creatSupplyRequest(supplyRequestDto, userDetails.getUser());
    }

}
