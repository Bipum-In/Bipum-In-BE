package com.sparta.bipuminbe.requests.controller;

import com.sparta.bipuminbe.common.security.UserDetailsImpl;
import com.sparta.bipuminbe.requests.dto.RepairRequestDto;
import com.sparta.bipuminbe.requests.service.RepairRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/requests")
public class RepairRequestController {

    private final RepairRequestService repairRequestService;

    @PostMapping("/repair")
    public ResponseEntity<RepairRequestDto> creatRepairRequest(@RequestBody RepairRequestDto repairRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return repairRequestService.creatRepairRequest(repairRequestDto, userDetails.getUser());
    }
}
