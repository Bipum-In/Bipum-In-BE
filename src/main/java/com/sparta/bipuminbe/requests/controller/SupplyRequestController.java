package com.sparta.bipuminbe.requests.controller;

import com.sparta.bipuminbe.requests.service.ReturnRequestService;
import com.sparta.bipuminbe.supply.service.SupplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SupplyRequestController {

    private final SupplyService supplyService;
}
